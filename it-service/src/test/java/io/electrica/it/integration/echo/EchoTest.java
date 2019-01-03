package io.electrica.it.integration.echo;

import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.sdk.java.api.Callback;
import io.electrica.sdk.java.api.Connection;
import io.electrica.sdk.java.api.Connector;
import io.electrica.sdk.java.api.Electrica;
import io.electrica.sdk.java.api.exception.IntegrationException;
import io.electrica.sdk.java.core.SingleInstanceHttpModule;
import io.electrica.sdk.java.echo.test.v1.EchoTestV1;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoTest extends BaseIT {

    private static Connection connection;
    private static AccessKeyDto accessKey;
    private static UserDto user;
    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";
    private static final String TEST_STRING = "Hello";
    private static Electrica instance;

    public void setUp() {
        createOrganization(ORG_HACKER_RANK);
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
    }

    // Since connection after each test, we need to create a new Connection object
    @BeforeEach
    void beforeEachTest() {
        if (user == null) {
            setUp();
        }
        ConnectorDto connectorDto = createEchoConnector("echo", "1");
        contextHolder.setContextForUser(user.getEmail());
        ConnectionDto connectionDto = createConnection(getCurrTimeAsString(), connectorDto, accessKey.getId());
        Connector sdkConnector = instance.connector(connectorDto.getErn());
        connection = sdkConnector.connection(connectionDto.getName());
    }

    @Test
    public void testPing() throws Exception {
        try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
            echoTest.ping();
        }
    }

    @Test
    public void testPingWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
                echoTest.ping(Boolean.TRUE);
            }
        });
    }

    @Test
    public void testPingWithTimeout() throws Exception {
        try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
            echoTest.ping(60L, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testPingWithTimeoutAndException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
                echoTest.ping(Boolean.TRUE, 60L, TimeUnit.SECONDS);
            }
        });
    }

    @Test
    public void testAsyncPing() throws Exception {
        AsyncResponseHandler<Void> voidHandler = new AsyncResponseHandler();
        try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
            echoTest.asyncPing(voidHandler);
            voidHandler.awaitResponse(60L, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testAsyncPingWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            AsyncResponseHandler<Void> voidHandler = new AsyncResponseHandler();
            try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
                echoTest.asyncPing(Boolean.TRUE, voidHandler);
                voidHandler.awaitResponse(60L, TimeUnit.SECONDS);
            }
        });
    }

    @Test
    public void testEcho() throws Exception {
        try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
            String result = echoTest.echo(TEST_STRING);
            assertEquals(result, TEST_STRING);
        }
    }

    @Test
    public void testEchoWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
                echoTest.echo(TEST_STRING, Boolean.TRUE);
            }
        });
    }

    @Test
    public void testEchoWithTimeout() throws Exception {
        try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
            String result = echoTest.echo(TEST_STRING, 60L, TimeUnit.SECONDS);
            assertEquals(result, TEST_STRING);
        }
    }

    @Test
    public void testEchoWithExceptionAndTimeout() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
                echoTest.echo(TEST_STRING, Boolean.TRUE, 60L, TimeUnit.SECONDS);
            }
        });
    }

    @Test
    public void testEchoWithCallback() throws Exception {
        AsyncResponseHandler<String> result = new AsyncResponseHandler<>();
        try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
            echoTest.asyncEcho(TEST_STRING, result);
            String out = result.awaitResponse(60L, TimeUnit.SECONDS);
            assertEquals(out, TEST_STRING);
        }
    }

    @Test
    public void testEchoWithCallbackException() {
        AsyncResponseHandler<String> result = new AsyncResponseHandler<>();
        Assertions.assertThrows(IntegrationException.class, () -> {
            try (EchoTestV1 echoTest = new EchoTestV1(connection)) {
                echoTest.asyncEcho(TEST_STRING, Boolean.TRUE, result);
                String out = result.awaitResponse(60L, TimeUnit.SECONDS);
                assertEquals(out, TEST_STRING);
            }
        });
    }


    private ConnectorDto createEchoConnector(String name, String version) {
        contextHolder.setContextForAdmin();
        Optional<ConnectorDto> optionalConnectorDto = connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getErn(), ECHO_CONNECTOR_ERN)).findFirst();
        ConnectorDto dto;
        if (optionalConnectorDto.isPresent()) {
            dto = optionalConnectorDto.get();
        } else {
            CreateConnectorDto createConnectorDto = new CreateConnectorDto(DEFAULT_CONNECTOR_TYPE,
                    AuthorizationType.Token, name, "test", version.toLowerCase(),
                    name, "https://echo.com", connectorUrl, sdkUrl,
                    "https://assets.themuse.com/uploaded/companies/773/small_logo.png", "test desciption",
                    TEST_CONNECTOR_PROPERTIES);
            dto = connectorClient.create(createConnectorDto).getBody();
        }
        return dto;
    }

    private static class AsyncResponseHandler<T> implements Callback<T> {

        private final BlockingQueue<Object[]> responseQueue = new ArrayBlockingQueue<>(1);

        @Override
        public void onResponse(T result) {
            responseQueue.add(new Object[]{result});
        }

        @Override
        public void onFailure(IntegrationException exception) {
            responseQueue.add(new Object[]{exception});
        }

        @SneakyThrows
        private String awaitResponse(Long timeout, TimeUnit unit) {
            Object[] responseContainer = responseQueue.poll(timeout, unit);
            if (responseContainer == null) {
                throw new TimeoutException();
            }
            Object o = responseContainer[0];
            String result = null;
            if (o instanceof String) {
                result = (String) o;
            } else if (o instanceof IntegrationException) {
                throw (IntegrationException) o;
            }
            return result;
        }
    }
}
