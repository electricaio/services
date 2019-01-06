package io.electrica.it.webhooks;

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
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class WebhookServiceTest extends BaseIT {

    private static final String WEBHOOK_PREFIX = "webhook-";
    private static final Map<String, String> TEST_WEBHOOK_PROPERTIES = Collections.singletonMap("a", "b");
    private UserDto user;

    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";

    @Inject
    ObjectMapper mapper;

    private Connection connection;
    private Electrica instance;
    private WebhookMessageHandler handler;
    private ConnectionWebhookDto webhookDto;

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        createWebhookConnection();
    }

    private void createWebhookConnection() {
        AccessKeyDto accessKey = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        ConnectorDto connectorDto = getEchoConnector();
        ConnectionDto connectionDto = createConnection(WEBHOOK_PREFIX + getCurrTimeAsString(), connectorDto,
                accessKey.getId());
        webhookDto = createAndSaveWebhook(connectionDto.getId(), accessKey.getId());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        Connector connector = instance.connector(ECHO_CONNECTOR_ERN);
        connection = connector.connection(connectionDto.getName());
        handler = new WebhookMessageHandler();
        connection.addMessageListener(x -> true, handler);
    }

    @BeforeEach
    public void setContext() {
        contextHolder.setContextForUser(user.getEmail());
    }

    @AfterAll
    void tearDown() {
        try {
            connection.close();
        } catch (Exception e) {
            Assert.fail("Exception while closing electrica instance:" + e.getMessage());
        }
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail("Exception while closing connection:" + e.getMessage());
        }
    }


    @Test
    public void testAddWebhook() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        createAndSaveWebhook(accessKey.getId());
    }

    @Test
    public void testGetByConnection() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        ConnectionWebhookDto webhookDto = webhookClient.getByConnection(connectionDtos.getId()).getBody().get(0);
        assertNotNull(webhookDto);
    }

    @Test
    public void testDeleteWebhook() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionWebhookDto dto = createAndSaveWebhook(accessKey.getId());
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        int count = webhookClient.getByConnection(connectionDtos.getId()).getBody().size();
        webhookClient.delete(dto.getId());
        int countAfterDeletion = webhookClient.getByConnection(connectionDtos.getId()).getBody().size();
        assertEquals(count - 1, countAfterDeletion);
    }

    private ConnectionWebhookDto createAndSaveWebhook(Long accessKeyId) {
        ConnectionDto connectionDto = connectionClient.findAllByAccessKeyId(accessKeyId).getBody().get(0);
        assertNotNull(connectionDto);
        return createAndSaveWebhook(connectionDto.getAccessKeyId(), accessKeyId);
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

    @Test
    public void testWebhookInvoke() {
        TokenDetails td = new TokenDetails();
        td.setAccessToken(instance.getAccessKey());
        contextHolder.setContext(td);
        JsonNode input = mapper.createObjectNode().put("a", "a");
        Object out = webhookClient.invoke2(webhookDto.getId(), input, 60000).getBody();
        assertNotNull(out);
    }

    @Test
    public void testWebhookSubmit() {
        TokenDetails td = new TokenDetails();
        td.setAccessToken(instance.getAccessKey());
        contextHolder.setContext(td);
        JsonNode input = mapper.createObjectNode().put("a", "a");
        webhookClient.submit(webhookDto.getId(), input).getBody();
        assertTrue(handler.awaitResponse());
    }

    private ConnectorDto getEchoConnector() {
        return connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getErn(), ECHO_CONNECTOR_ERN))
                .findFirst().get();
    }

    private class WebhookMessageHandler implements MessageListener {

        private final BlockingQueue<Boolean> queue = new ArrayBlockingQueue<>(1);

        @Override
        @SneakyThrows
        public Optional<Object> onMessage(Message message) {
            Result result = new Result("Test");
            JsonNode out = mapper.convertValue(result, JsonNode.class);
            queue.add(Boolean.TRUE);
            return Optional.of(out);
        }

        @SneakyThrows
        private Boolean awaitResponse() {
            return queue.poll(60, TimeUnit.SECONDS);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    static class Result {
        public String result;
    }


}
