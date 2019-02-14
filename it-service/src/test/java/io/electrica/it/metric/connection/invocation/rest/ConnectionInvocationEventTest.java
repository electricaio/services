package io.electrica.it.metric.connection.invocation.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.ImmutableMap;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.metric.connection.invocation.dto.ConnectionInvocationDto;
import io.electrica.metric.connection.invocation.feign.ConnectionInvocationClient;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.sdk.java8.api.Connection;
import io.electrica.sdk.java8.api.Connector;
import io.electrica.sdk.java8.api.Electrica;
import io.electrica.sdk.java8.api.exception.IntegrationException;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.sdk.java8.echo.test.v1.EchoTestV1;
import io.electrica.sdk.java8.echo.test.v1.model.EchoTestV1Action;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class ConnectionInvocationEventTest extends BaseIT {

    private static final String TEST_STRING = "Hello";
    private static final String ECHO_TEST_CONNECTOR_ERN = "ern://echo:test:1";
    private Electrica instance;
    private EchoTestV1 echoTest;
    private UserDto user;
    private AccessKeyDto accessKey;
    private ConnectorDto connectorDto;
    private ConnectionDto connectionDto;
    @Inject
    private ConnectionInvocationClient connectionInvocationClient;
    @Inject
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setTokenForUser(user.getEmail());
        accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(standUrl), fullAccessKeyDto.getKey());
        connectorDto = getEchoConnector();
        connectionDto = createConnection(getCurrTimeAsString(), connectorDto, accessKey.getId());
        Connector sdkConnector = instance.connector(connectorDto.getErn());
        Connection connection = sdkConnector.connection(connectionDto.getName());
        echoTest = new EchoTestV1(connection);
        contextHolder.setTokenForAdmin();
    }


    @AfterAll
    void tearDown() {
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail("Instance close with Exception:" + e.getMessage());
        }
    }

    @Test
    void testSuccess() throws Exception {
        echoTest.echo(TEST_STRING);
        ConnectionInvocationDto connectionInvocation = getConnectionInvocation(instance.getInstanceId(),
                ConnectionInvocationStatus.Success);
        assertEquals(user.getId(), connectionInvocation.getUserId());
        assertEquals(accessKey.getId(), connectionInvocation.getAccessKeyId());
        assertEquals(user.getOrganizationId(), connectionInvocation.getOrganizationId());
        assertEquals(connectionDto.getId(), connectionInvocation.getConnectionId());
        assertEquals(connectorDto.getId(), connectionInvocation.getConnectorId());
        assertEquals(connectorDto.getErn(), connectionInvocation.getErn());
        assertEquals(EchoTestV1Action.SEND.getValue(), connectionInvocation.getAction());
        JsonNode expectedPayloadResult = objectMapper.valueToTree(ImmutableMap.of("message", TEST_STRING));
        assertEquals(expectedPayloadResult, connectionInvocation.getPayload());
        assertEquals(expectedPayloadResult, connectionInvocation.getResult());
        assertEquals(objectMapper.valueToTree(ImmutableMap.of("throwException", false)),
                connectionInvocation.getParameters());
    }

    @Test
    void testException() throws Exception {
        Assertions.assertThrows(IntegrationException.class, () -> echoTest.echo(TEST_STRING, Boolean.TRUE));
        ConnectionInvocationDto connectionInvocation = getConnectionInvocation(instance.getInstanceId(),
                ConnectionInvocationStatus.Error);
        assertEquals(user.getId(), connectionInvocation.getUserId());
        assertEquals(accessKey.getId(), connectionInvocation.getAccessKeyId());
        assertEquals(user.getOrganizationId(), connectionInvocation.getOrganizationId());
        assertEquals(connectionDto.getId(), connectionInvocation.getConnectionId());
        assertEquals(connectorDto.getId(), connectionInvocation.getConnectorId());
        assertEquals(connectorDto.getErn(), connectionInvocation.getErn());
        assertEquals(EchoTestV1Action.SEND.getValue(), connectionInvocation.getAction());
        JsonNode expectedPayload = objectMapper.valueToTree(ImmutableMap.of("message", TEST_STRING));
        assertEquals(expectedPayload, connectionInvocation.getPayload());
        assertEquals(NullNode.instance, connectionInvocation.getResult());
        assertEquals(objectMapper.valueToTree(ImmutableMap.of("throwException", true)),
                connectionInvocation.getParameters());
        assertEquals("generic", connectionInvocation.getErrorCode());
        assertTrue(connectionInvocation.getStackTrace() != null
                && !connectionInvocation.getStackTrace().isEmpty());
    }

    private ConnectionInvocationDto getConnectionInvocation(UUID instanceId, ConnectionInvocationStatus status)
            throws InterruptedException {
        Optional<ConnectionInvocationDto> connectionInvocationDto = Optional.empty();
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            connectionInvocationDto = connectionInvocationClient.feignGetConnectionInvocations(
                    0, 1, null, null, null, instanceId,
                    null, null, Collections.singleton(status),
                    null, null
            ).stream().findFirst();
            if (connectionInvocationDto
                    //present action means ConnectionInvocationEvent was stored
                    //check it for cases when result was stored faster then invocation
                    .filter(dto -> dto.getAction() != null && dto.getStatus() == status)
                    .isPresent()) {
                break;
            }
        }
        return connectionInvocationDto
                .orElseThrow(() -> new IllegalArgumentException("Connection Invocation not found"));
    }

    private ConnectorDto getEchoConnector() {
        return connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getErn(), ECHO_TEST_CONNECTOR_ERN))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Echo connection not found"));
    }
}

