package io.electrica.it.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class WebhookInvokeTest extends BaseIT {


    private static final String WEBHOOK_PREFIX = "webhook-";
    private static final Map<String, String> TEST_WEBHOOK_PROPERTIES = Collections.singletonMap("a", "b");
    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";

    @Inject
    ObjectMapper mapper;

    private Connection connection;
    private Connector connector;
    private Electrica instance;
    private ConnectionWebhookDto webhookDto;
    private ConnectionDto connectionDto;
    private UserDto user;
    private AccessKeyDto accessKey;
    private JsonNode input;
    private ConnectorDto echoConnector;
    UUID listenerUUID;

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        accessKey = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        echoConnector = getConnectorForErn(ECHO_CONNECTOR_ERN);
        createWebhookConnection();
        input = mapper.valueToTree(mapper.createObjectNode().put("a", "b"));
        TokenDetails td = new TokenDetails();
        td.setAccessToken(instance.getAccessKey());
        contextHolder.setContext(td);
        connection = connector.connection(connectionDto.getName());
    }

    @AfterAll
    void tearDown() {
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail("Exception while closing connection:" + e.getMessage());
        }
    }

    @AfterEach
    void clearContext() {
        connection.removeMessageListener(listenerUUID);
    }

    @Test
    public void testWebhookInvokeAndReturnObject() throws JsonProcessingException, InterruptedException {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.of(new Result("Test"));
        });
        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), input, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertMessage(message, true);
        assertEquals(TEST_WEBHOOK_PROPERTIES, message.getPayload(Map.class));
        Result result = mapper.treeToValue(out, Result.class);
        assertEquals("Test", result.getResult());
    }


    @Test
    public void testWebhookInvokeWithJsonInputAndReturnObject() throws JsonProcessingException, InterruptedException {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.of(new Result("Test"));
        });
        Input input = new Input("Testinput");
        JsonNode jsonNode = mapper.convertValue(input, JsonNode.class);
        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), jsonNode, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertEquals("Testinput", message.getPayload(Input.class).getInput());
        Result result = mapper.treeToValue(out, Result.class);
        assertEquals("Test", result.getResult());
    }

    @Test
    public void testWebhookInvokeAndReturnString() throws JsonProcessingException, InterruptedException {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.of("Test");
        });

        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), input, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertMessage(message, true);
        assertEquals(TEST_WEBHOOK_PROPERTIES, message.getPayload(Map.class));
        String result = mapper.treeToValue(out, String.class);
        assertEquals("Test", result);
    }

    @Test
    public void testWebhookInvokeAndReturnNull() throws InterruptedException {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.empty();
        });
        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), input, 60000).getBody();
        Message message = awaitResultFromQueue(queue);
        assertMessage(message, true);
        assertEquals(TEST_WEBHOOK_PROPERTIES, message.getPayload(Map.class));
        assertEquals("null", out.asText());
    }

    @Test
    public void testWebhookSubmit() throws InterruptedException {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
        listenerUUID = connection.addMessageListener(p -> true, message -> {
            queue.add(message);
            return Optional.empty();
        });
        webhookClient.submit(webhookDto.getId(), input).getBody();
        Message message = awaitResultFromQueue(queue);
        assertMessage(message, false);
        assertEquals(TEST_WEBHOOK_PROPERTIES, message.getPayload(Map.class));
    }

    private void createWebhookConnection() {
        connectionDto = createConnection(WEBHOOK_PREFIX + getCurrTimeAsString(), echoConnector,
                accessKey.getId());
        webhookDto = createAndSaveWebhook(connectionDto.getId(), accessKey.getId());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        connector = instance.connector(ECHO_CONNECTOR_ERN);
    }

    private ConnectionWebhookDto createAndSaveWebhook(Long connectionId, Long accessKeyId) {
        ConnectionCreateWebhookDto webhookDto = new ConnectionCreateWebhookDto();
        webhookDto.setAccessKeyId(accessKeyId);
        webhookDto.setConnectionId(connectionId);
        webhookDto.setName(WEBHOOK_PREFIX + getCurrTimeInMillSeconds());
        webhookDto.setProperties(TEST_WEBHOOK_PROPERTIES);
        ConnectionWebhookDto response = webhookClient.createConnection(webhookDto).getBody();
        assertNotNull(response.getId());
        assertNotNull(response.getUrl());
        assertEquals(webhookDto.getName(), response.getName());
        assertEquals(webhookDto.getAccessKeyId(), response.getAccessKeyId());
        assertEquals(webhookDto.getConnectionId(), response.getConnectionId());
        return response;
    }

    private void assertMessage(Message message, boolean isExpectedResult) {
        assertNotNull(message.getId());
        assertEquals(webhookDto.getId(), message.getWebhookId());
        assertNotNull(message.getWebhookServiceId());
        assertEquals(webhookDto.getName(), message.getName());
        assertEquals(Message.Scope.Connection, message.getScope());
        assertNull(message.getConnectorId());
        assertNull(message.getConnectorErn());
        assertEquals(connection.getId(), message.getConnectionId());
        assertEquals(TEST_WEBHOOK_PROPERTIES, message.getPropertiesMap());
        assertEquals(isExpectedResult, message.getExpectedResult());
    }

    private <T> T awaitResultFromQueue(BlockingQueue<T> queue) throws InterruptedException {
        T result = queue.poll(30, TimeUnit.SECONDS);
        assertNotNull(result, "Waiting of result timed out");
        return result;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Result {
        private String result;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Input {
        private String input;
    }
}
