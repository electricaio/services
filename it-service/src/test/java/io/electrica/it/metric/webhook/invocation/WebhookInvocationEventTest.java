package io.electrica.it.metric.webhook.invocation;

import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.it.auth.TokenDetails;
import io.electrica.it.webhook.feign.WebhookTestClient;
import io.electrica.metric.connection.invocation.feign.ConnectionInvocationClient;
import io.electrica.metric.webhook.invocation.dto.WebhookInvocationDto;
import io.electrica.metric.webhook.invocation.feign.WebhookInvocationClient;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import io.electrica.sdk.java8.api.Connection;
import io.electrica.sdk.java8.api.Connector;
import io.electrica.sdk.java8.api.Electrica;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import io.electrica.webhook.dto.WebhookScope;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class WebhookInvocationEventTest extends BaseIT {
    private static final String PAYLOAD_TEST_STRING = "someTestString {\"some\":\"string\"} <element></element>";
    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";
    private static final String WEBHOOK_PREFIX = "webhook-";

    private Electrica instance;
    private UserDto user;
    private AccessKeyDto accessKey;
    private ConnectorDto connectorDto;
    private ConnectionDto connectionDto;
    private ConnectionWebhookDto webhookDto;
    private Connection connection;
    private UUID listenerUUID;

    @Inject
    private WebhookTestClient webhookClient;
    @Inject
    private ConnectionInvocationClient connectionInvocationClient;
    @Inject
    private WebhookInvocationClient webhookInvocationClient;

    @BeforeAll
    void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setTokenForUser(user.getEmail());
        accessKey = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        connectorDto = findConnector(ECHO_CONNECTOR_ERN);
        connectionDto = createConnection(
                WEBHOOK_PREFIX + getCurrTimeAsString(),
                connectorDto,
                accessKey.getId()
        );
        webhookDto = createConnectionWebhook(connectionDto.getId(), accessKey.getId());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        Connector connector = instance.connector(ECHO_CONNECTOR_ERN);
        connection = connector.connection(connectionDto.getName());
    }

    @BeforeEach
    void setUpToken() {
        TokenDetails token = TokenDetails.builder().accessToken(instance.getAccessKey()).build();
        contextHolder.setToken(token);
    }

    @AfterAll
    void tearDown() {
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail("Instance close with Exception:" + e.getMessage());
        }
    }

    @AfterEach
    void clearContext() {
        connection.removeMessageListener(listenerUUID);
    }

    @Test
    void testWebhookInvocationSuccess() throws InterruptedException {
        listenerUUID = connection.addMessageListener(__ -> true, __ -> PAYLOAD_TEST_STRING);
        webhookClient.invoke(webhookDto.getId(), PAYLOAD_TEST_STRING, 60000).getBody();
        WebhookInvocationDto webhookInvocationDto = find(webhookDto.getId(), WebhookInvocationStatus.Success);
        commonAssert(webhookInvocationDto);
        assertEquals(instance.getInstanceId(), webhookInvocationDto.getSdkInstanceId());
        assertEquals(PAYLOAD_TEST_STRING, webhookInvocationDto.getResultPayload());
        assertNull(webhookInvocationDto.getErrorMessage());
        assertNull(webhookInvocationDto.getErrorTime());
    }

    @Test
    void testWebhookInvocationTimeoutError() throws InterruptedException {
        listenerUUID = connection.addMessageListener(__ -> true, __ -> {
            throw new RuntimeException("Test");
        });
        assertThrows(FeignException.class,
                () -> webhookClient.invoke(webhookDto.getId(), PAYLOAD_TEST_STRING, 123));
        WebhookInvocationDto webhookInvocationDto = find(webhookDto.getId(), WebhookInvocationStatus.Error);
        commonAssert(webhookInvocationDto);
        assertNull(webhookInvocationDto.getResultPayload());
        assertEquals("Webhook timeout exception 123ms", webhookInvocationDto.getErrorMessage());
        assertNotNull(webhookInvocationDto.getErrorTime());
    }

    @Test
    void testWebhookInvocationWithoutResult() throws InterruptedException {
        listenerUUID = connection.addMessageListener(__ -> true, __ -> "");
        webhookClient.submit(webhookDto.getId(), PAYLOAD_TEST_STRING);
        WebhookInvocationDto webhookInvocationDto = find(webhookDto.getId(), WebhookInvocationStatus.Invoked);
        commonAssert(webhookInvocationDto);
        assertNull(webhookInvocationDto.getResultPayload());
        assertNull(webhookInvocationDto.getErrorMessage());
        assertNull(webhookInvocationDto.getErrorTime());
    }

    private void commonAssert(WebhookInvocationDto webhookInvocationDto) {
        assertEquals(user.getId(), webhookInvocationDto.getUserId());
        assertEquals(user.getOrganizationId(), webhookInvocationDto.getOrganizationId());
        assertEquals(accessKey.getId(), webhookInvocationDto.getAccessKeyId());
        assertEquals(connectionDto.getId(), webhookInvocationDto.getConnectionId());
        assertEquals(webhookDto.getProperties(), webhookInvocationDto.getProperties());
        assertEquals(WebhookScope.Connection, webhookInvocationDto.getScope());
        assertEquals(webhookDto.getIsPublic(), webhookInvocationDto.getIsPublic());
        assertEquals(webhookDto.getId(), webhookInvocationDto.getWebhookId());
        assertEquals(webhookDto.getName(), webhookInvocationDto.getWebhookName());
        assertEquals(PAYLOAD_TEST_STRING, webhookInvocationDto.getPayload());
    }

    private WebhookInvocationDto find(UUID webhookId, WebhookInvocationStatus status) throws InterruptedException {
        contextHolder.setTokenForUser(user.getEmail());
        Optional<WebhookInvocationDto> webhookInvocationDto = Optional.empty();
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            webhookInvocationDto = webhookInvocationClient.feignGetWebhookInvocations(0, 1,
                    null, null, null, webhookId, null,
                    Collections.singleton(status), null, null).stream().findFirst();
            if (webhookInvocationDto.filter(e -> e.getStartTime() != null).isPresent()) {
                break;
            }
        }
        return webhookInvocationDto.orElseThrow(() -> new IllegalArgumentException("Webhook Invocation not found"));
    }

    private ConnectionWebhookDto createConnectionWebhook(Long connectionId, Long accessKeyId) {
        ConnectionCreateWebhookDto wh = new ConnectionCreateWebhookDto();
        wh.setAccessKeyId(accessKeyId);
        wh.setConnectionId(connectionId);
        wh.setIsPublic(false);
        wh.setName(WEBHOOK_PREFIX + getCurrTimeInMillSeconds());
        wh.setProperties(ImmutableMap.of("Key", "Value"));
        return webhookManagementClient.createConnection(wh).getBody();
    }
}
