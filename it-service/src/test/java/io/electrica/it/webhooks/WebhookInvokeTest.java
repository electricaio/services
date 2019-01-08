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
import io.electrica.sdk.java8.api.MessageListener;
import io.electrica.sdk.java8.api.http.Message;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import lombok.*;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
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

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        accessKey = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        createWebhookConnection();
        input = mapper.valueToTree(mapper.createObjectNode().put("a", "a"));
        TokenDetails td = new TokenDetails();
        td.setAccessToken(instance.getAccessKey());
        contextHolder.setContext(td);
    }

    @AfterAll
    void tearDown() {
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail("Exception while closing connection:" + e.getMessage());
        }
    }

    @Test
    public void testWebhookInvokeAndReturnObject() throws JsonProcessingException {
        UUID listenerUUID = addListenerToconnection(new WebhookMessageListenerReturnObject());
        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), input, 60000).getBody();
        Result result = mapper.treeToValue(out, Result.class);
        assertEquals("Test", result.getResult());
        connection.removeMessageListener(listenerUUID);
    }

    @Test
    public void testWebhookInvokeAndReturnString() throws JsonProcessingException {
        UUID listenerUUID = addListenerToconnection(new WebhookMessageListenerReturnString());
        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), input, 60000).getBody();
        String result = mapper.treeToValue(out, String.class);
        assertEquals("Test", result);
        connection.removeMessageListener(listenerUUID);
    }

    @Test
    public void testWebhookInvokeAndReturnNull() throws JsonProcessingException {
        UUID listenerUUID = addListenerToconnection(new WebhookMessageListenerReturnNull());
        JsonNode out = webhookInvokeClient.invoke(webhookDto.getId(), input, 60000).getBody();
        assertEquals("null", out.asText());
        connection.removeMessageListener(listenerUUID);
    }

    @Test
    public void testWebhookSubmit() {
        WebhookMessageListenerReturnObject listener = new WebhookMessageListenerReturnObject();
        UUID listenerUUID = addListenerToconnection(listener);
        webhookClient.submit(webhookDto.getId(), input).getBody();
        assertTrue(listener.awaitResponse());
        connection.removeMessageListener(listenerUUID);
    }

    private void createWebhookConnection() {

        ConnectorDto connectorDto = getConnectorForErn(ECHO_CONNECTOR_ERN);
        connectionDto = createConnection(WEBHOOK_PREFIX + getCurrTimeAsString(), connectorDto,
                accessKey.getId());
        webhookDto = createAndSaveWebhook(connectionDto.getId(), accessKey.getId());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        connector = instance.connector(ECHO_CONNECTOR_ERN);
    }

    private UUID addListenerToconnection(MessageListener listener) {
        connection = connector.connection(connectionDto.getName());
        return connection.addMessageListener(x -> true, listener);
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


    private class WebhookMessageListenerReturnObject implements MessageListener {

        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        @SneakyThrows
        public Optional<Object> onMessage(Message message) {
            assertEquals(webhookDto.getId(), message.getWebhookId());
            assertEquals(webhookDto.getConnectionId(), message.getConnectionId());
            assertEquals("a", mapper.convertValue(message.getPayload(Map.class), JsonNode.class).get("a").asText());
            latch.countDown();
            if (message.getExpectedResult()) {
                Result result = new Result("Test");
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        }

        @SneakyThrows
        private Boolean awaitResponse() {
            return latch.await(60, TimeUnit.SECONDS);
        }
    }

    private class WebhookMessageListenerReturnString implements MessageListener {

        @Override
        @SneakyThrows
        public Optional<Object> onMessage(Message message) {
            assertEquals(webhookDto.getId(), message.getWebhookId());
            assertEquals(webhookDto.getConnectionId(), message.getConnectionId());
            assertEquals("a", mapper.convertValue(message.getPayload(Map.class), JsonNode.class).get("a").asText());
            if (message.getExpectedResult()) {
                return Optional.of("Test");
            } else {
                return Optional.empty();
            }
        }
    }

    private class WebhookMessageListenerReturnNull implements MessageListener {

        @Override
        @SneakyThrows
        public Optional<Object> onMessage(Message message) {
            assertEquals(webhookDto.getId(), message.getWebhookId());
            assertEquals(webhookDto.getConnectionId(), message.getConnectionId());
            assertEquals("a", mapper.convertValue(message.getPayload(Map.class), JsonNode.class).get("a").asText());
            Object o = null;
            if (message.getExpectedResult()) {
                return Optional.ofNullable(o);
            } else {
                return Optional.empty();
            }
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Result {
        public String result;
    }
}
