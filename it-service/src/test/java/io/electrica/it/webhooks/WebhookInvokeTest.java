package io.electrica.it.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.it.auth.TokenDetails;
import io.electrica.sdk.java8.api.Connection;
import io.electrica.sdk.java8.api.Connector;
import io.electrica.sdk.java8.api.Electrica;
import io.electrica.sdk.java8.api.http.Message;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import lombok.*;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class WebhookInvokeTest extends BaseIT {

    private static final String WEBHOOK_PREFIX = "webhook-";
    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";

    private static final String MESSAGE_STRING_RESULT = "someTestString";
    private static final Result MESSAGE_OBJECT_RESULT = new Result(MESSAGE_STRING_RESULT);
    private static final Map<String, String> PAYLOD = Collections.singletonMap("stringProperty", "stringValue");
    private static final Map<String, String> WEBHOOK_PROPERTIES = Collections.singletonMap("Key", "Value");

    @Inject
    private ObjectMapper mapper;

    private Connection connection;
    private Electrica instance;
    private ConnectionWebhookDto privateWebhookDto;
    private ConnectionWebhookDto publicWebhookDto;
    private List<ConnectionWebhookDto> allWebhooks;
    private JsonNode payload;
    private UUID listenerUUID;

    private static <T> T awaitResultFromQueue(BlockingQueue<T> queue) throws InterruptedException {
        T result = queue.poll(30, TimeUnit.SECONDS);
        assertNotNull(result, "Waiting of result timed out");
        return result;
    }

    private static String getWebhookSign(ConnectionWebhookDto webhook) {
        String url = webhook.getPublicInvokeUrl();
        String s = url.replaceFirst("/invoke", "");
        return s.substring(s.lastIndexOf("/") + 1);
    }

    @BeforeAll
    void setUp() {
        init();

        UserDto user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setTokenForUser(user.getEmail());

        AccessKeyDto accessKey = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        ConnectorDto echoConnector = findConnector(ECHO_CONNECTOR_ERN);
        ConnectionDto connectionDto = createConnection(
                WEBHOOK_PREFIX + getCurrTimeAsString(),
                echoConnector,
                accessKey.getId()
        );
        privateWebhookDto = createConnectionWebhook(connectionDto.getId(), accessKey.getId(), false);
        publicWebhookDto = createConnectionWebhook(connectionDto.getId(), accessKey.getId(), true);
        allWebhooks = Arrays.asList(privateWebhookDto, publicWebhookDto);

        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        Connector connector = instance.connector(ECHO_CONNECTOR_ERN);
        payload = mapper.valueToTree(PAYLOD);
        connection = connector.connection(connectionDto.getName());
    }

    @AfterAll
    void tearDown() throws Exception {
        instance.close();
    }

    @BeforeEach
    void setUpToken() {
        TokenDetails token = TokenDetails.builder().accessToken(instance.getAccessKey()).build();
        contextHolder.setToken(token);
    }

    @AfterEach
    void clearContext() {
        connection.removeMessageListener(listenerUUID);
    }

    private ConnectionWebhookDto createConnectionWebhook(Long connectionId, Long accessKeyId, boolean isPublic) {
        ConnectionCreateWebhookDto wh = new ConnectionCreateWebhookDto();
        wh.setAccessKeyId(accessKeyId);
        wh.setConnectionId(connectionId);
        wh.setIsPublic(isPublic);
        wh.setName(WEBHOOK_PREFIX + getCurrTimeInMillSeconds());
        wh.setProperties(WEBHOOK_PROPERTIES);
        return webhookManagementClient.createConnection(wh).getBody();
    }

    @Test
    void testWebhookInvokeAndReturnObject() throws Exception {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.of(MESSAGE_OBJECT_RESULT);
        });

        for (ConnectionWebhookDto webhook : allWebhooks) {
            JsonNode response = webhookClient.feignInvoke(webhook.getId(), payload, 60000).getBody();
            Message message = awaitResultFromQueue(queue);
            assertInvokeAndReturnObject(message, webhook, response);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.feignPublicInvoke(privateWebhookDto.getId(), sign, payload, 60000).getBody();
        });

        JsonNode response = webhookClient.feignPublicInvoke(publicWebhookDto.getId(), sign, payload, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertInvokeAndReturnObject(message, publicWebhookDto, response);
    }

    private void assertInvokeAndReturnObject(Message message, ConnectionWebhookDto webhook, JsonNode response)
            throws Exception {
        assertConnectionMessage(webhook, message, true);
        assertNotNull(response);
        Result messageResult = mapper.treeToValue(response, Result.class);
        assertEquals(MESSAGE_OBJECT_RESULT, messageResult);
    }

    @Test
    void testWebhookInvokeAndReturnString() throws Exception {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.of(MESSAGE_STRING_RESULT);
        });

        for (ConnectionWebhookDto webhook : allWebhooks) {
            JsonNode response = webhookClient.feignInvoke(webhook.getId(), payload, 60000).getBody();
            Message message = awaitResultFromQueue(queue);
            assertInvokeAndReturnString(message, webhook, response);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.feignPublicInvoke(privateWebhookDto.getId(), sign, payload, 60000).getBody();
        });

        JsonNode response = webhookClient.feignPublicInvoke(publicWebhookDto.getId(), sign, payload, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertInvokeAndReturnString(message, publicWebhookDto, response);
    }

    private void assertInvokeAndReturnString(Message message, ConnectionWebhookDto webhook, JsonNode response)
            throws Exception {
        assertConnectionMessage(webhook, message, true);
        assertNotNull(response);
        String messageResult = mapper.treeToValue(response, String.class);
        assertEquals(MESSAGE_STRING_RESULT, messageResult);
    }

    @Test
    void testWebhookInvokeAndReturnNull() throws Exception {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.empty();
        });

        for (ConnectionWebhookDto webhook : allWebhooks) {
            JsonNode response = webhookClient.feignInvoke(webhook.getId(), payload, 60000).getBody();
            Message message = awaitResultFromQueue(queue);
            assertInvokeAndReturnNull(message, webhook, response);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.feignPublicInvoke(privateWebhookDto.getId(), sign, payload, 60000).getBody();
        });

        JsonNode response = webhookClient.feignPublicInvoke(publicWebhookDto.getId(), sign, payload, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertInvokeAndReturnNull(message, publicWebhookDto, response);
    }

    private void assertInvokeAndReturnNull(Message message, ConnectionWebhookDto webhook, JsonNode response)
            throws Exception {
        assertConnectionMessage(webhook, message, true);
        assertNotNull(response);
        String messageResult = mapper.treeToValue(response, String.class);
        assertNull(messageResult);
    }

    @Test
    void testWebhookSubmit() throws InterruptedException {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.empty();
        });

        for (ConnectionWebhookDto webhook : allWebhooks) {
            webhookClient.feignSubmit(webhook.getId(), payload).getBody();
            Message message = awaitResultFromQueue(queue);
            assertConnectionMessage(webhook, message, false);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.feignPublicInvoke(privateWebhookDto.getId(), sign, payload, 60000).getBody();
        });

        webhookClient.feignPublicSubmit(publicWebhookDto.getId(), sign, payload).getBody();
        Message message = awaitResultFromQueue(queue);
        assertConnectionMessage(publicWebhookDto, message, false);
    }

    private void assertConnectionMessage(ConnectionWebhookDto webhook, Message message, boolean isExpectedResult) {
        assertNotNull(message.getId());
        assertEquals(webhook.getId(), message.getWebhookId());
        assertNotNull(message.getWebhookServiceId());
        assertEquals(webhook.getName(), message.getName());
        assertEquals(webhook.getOrganizationId(), message.getOrganizationId());
        assertEquals(webhook.getUserId(), message.getUserId());
        assertEquals(webhook.getAccessKeyId(), message.getAccessKeyId());
        assertEquals(webhook.getIsPublic(), message.isPublic());
        assertEquals(Message.Scope.Connection, message.getScope());
        assertNull(message.getConnectorId());
        assertNull(message.getConnectorErn());
        assertEquals(connection.getId(), message.getConnectionId());
        assertEquals(WEBHOOK_PROPERTIES, message.getPropertiesMap());
        assertEquals(isExpectedResult, message.getExpectedResult());
        assertEquals(PAYLOD, message.getPayload(Map.class));
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String result;
    }
}
