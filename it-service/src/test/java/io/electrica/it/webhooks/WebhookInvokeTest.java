package io.electrica.it.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.it.auth.TokenDetails;
import io.electrica.it.webhook.feign.WebhookTestClient;
import io.electrica.sdk.java8.api.Connection;
import io.electrica.sdk.java8.api.Connector;
import io.electrica.sdk.java8.api.Electrica;
import io.electrica.sdk.java8.api.MessageListener;
import io.electrica.sdk.java8.api.http.Message;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class WebhookInvokeTest extends BaseIT {

    private static final String WEBHOOK_PREFIX = "webhook-";
    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";

    private static final String PAYLOAD_TEST_STRING = "someTestString {\"some\":\"string\"} <element></element>";
    private static final Map<String, String> WEBHOOK_PROPERTIES = Collections.singletonMap("Key", "Value");

    @Inject
    private ObjectMapper mapper;
    @Inject
    private WebhookTestClient webhookClient;

    private Connection connection;
    private Electrica instance;
    private ConnectionWebhookDto privateWebhookDto;
    private ConnectionWebhookDto publicWebhookDto;
    private List<ConnectionWebhookDto> allWebhooks;
    private UUID listenerUUID;

    private static <T> T awaitResultFromQueue(BlockingQueue<T> queue) {
        try {
            T result = queue.poll(30, TimeUnit.SECONDS);
            assertNotNull(result, "Waiting of result timed out");
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        instance = Electrica.instance(new SingleInstanceHttpModule(standUrl), fullAccessKeyDto.getKey());
        Connector connector = instance.connector(ECHO_CONNECTOR_ERN);
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

    private Supplier<Message> addListenerAndSetUUID(Predicate<Message> filter, MessageListener listener) {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);

        listenerUUID = connection.addMessageListener(filter, message -> {
            queue.add(message);
            return listener.onMessage(message);
        });

        return () -> awaitResultFromQueue(queue);
    }

    @Test
    void testWebhookInvokeWithDifferentTypes() {
        Supplier<Message> messageSupplier = addListenerAndSetUUID(__ -> true, __ -> PAYLOAD_TEST_STRING);

        for (ConnectionWebhookDto webhook : allWebhooks) {
            String response = webhookClient.invokeJson(webhook.getId(), PAYLOAD_TEST_STRING, 60000).getBody();
            assertInvokeAndReturnString(messageSupplier.get(), webhook, response,
                    "application/json", "application/json");

            response = webhookClient.invokeXml(webhook.getId(), PAYLOAD_TEST_STRING, 60000).getBody();
            assertInvokeAndReturnString(messageSupplier.get(), webhook, response,
                    "application/xml", "application/xml");

            response = webhookClient.invokeTextPlain(webhook.getId(), PAYLOAD_TEST_STRING, 60000).getBody();
            assertInvokeAndReturnString(messageSupplier.get(), webhook, response,
                    "text/plain", "text/plain");
        }
    }

    @Test
    void testWebhookInvokeAndReturnString() {
        Supplier<Message> messageSupplier = addListenerAndSetUUID(__ -> true, __ -> PAYLOAD_TEST_STRING);

        for (ConnectionWebhookDto webhook : allWebhooks) {
            String response = webhookClient.invoke(webhook.getId(), PAYLOAD_TEST_STRING, 60000).getBody();
            Message message = messageSupplier.get();
            assertInvokeAndReturnString(message, webhook, response);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.publicInvoke(privateWebhookDto.getId(), sign, PAYLOAD_TEST_STRING, 60000).getBody();
        });

        String response = webhookClient.publicInvoke(publicWebhookDto.getId(),
                sign, PAYLOAD_TEST_STRING, 60000).getBody();
        Message message = messageSupplier.get();
        assertInvokeAndReturnString(message, publicWebhookDto, response);
    }

    private void assertInvokeAndReturnString(Message message, ConnectionWebhookDto webhook, String response,
                                             String contentType, String expectedContentType) {
        assertEquals(contentType, message.getContentType());
        assertEquals(expectedContentType, message.getExpectedContentType());
        assertInvokeAndReturnString(message, webhook, response);
    }

    private void assertInvokeAndReturnString(Message message, ConnectionWebhookDto webhook, String response) {
        assertConnectionMessage(webhook, message, true);
        assertNotNull(response);
        assertEquals(PAYLOAD_TEST_STRING, response);
    }

    @Test
    void testWebhookInvokeAndReturnNull() {
        Supplier<Message> messageSupplier = addListenerAndSetUUID(__ -> true, __ -> null);

        for (ConnectionWebhookDto webhook : allWebhooks) {
            String response = webhookClient.invoke(webhook.getId(), PAYLOAD_TEST_STRING, 60000).getBody();
            assertInvokeAndReturnNull(messageSupplier.get(), webhook, response);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.publicInvoke(privateWebhookDto.getId(), sign, PAYLOAD_TEST_STRING, 60000).getBody();
        });

        String response = webhookClient.publicInvoke(publicWebhookDto.getId(), sign,
                PAYLOAD_TEST_STRING, 60000).getBody();
        assertInvokeAndReturnNull(messageSupplier.get(), publicWebhookDto, response);
    }

    private void assertInvokeAndReturnNull(Message message, ConnectionWebhookDto webhook, String response) {
        assertConnectionMessage(webhook, message, true);
        assertNull(response);
    }

    @Test
    void testWebhookSubmit() {
        Supplier<Message> messageSupplier = addListenerAndSetUUID(__ -> true, __ -> null);

        for (ConnectionWebhookDto webhook : allWebhooks) {
            webhookClient.submit(webhook.getId(), PAYLOAD_TEST_STRING).getBody();
            assertConnectionMessage(webhook, messageSupplier.get(), false);
        }

        contextHolder.setToken(null);
        String sign = getWebhookSign(publicWebhookDto);

        assertThrows(FeignException.class, () -> {
            webhookClient.publicInvoke(privateWebhookDto.getId(), sign, PAYLOAD_TEST_STRING, 60000).getBody();
        });

        webhookClient.publicSubmit(publicWebhookDto.getId(), sign, PAYLOAD_TEST_STRING).getBody();
        assertConnectionMessage(publicWebhookDto, messageSupplier.get(), false);
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
        assertEquals(PAYLOAD_TEST_STRING, message.getPayload());
    }
}
