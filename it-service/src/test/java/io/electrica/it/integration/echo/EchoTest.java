package io.electrica.it.integration.echo;

import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.sdk.java8.api.Callback;
import io.electrica.sdk.java8.api.Connection;
import io.electrica.sdk.java8.api.Connector;
import io.electrica.sdk.java8.api.Electrica;
import io.electrica.sdk.java8.api.exception.IntegrationException;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.sdk.java8.echo.test.v1.EchoTestV1;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class EchoTest extends BaseIT {

    private static final String TEST_STRING = "Hello";
    private static final String ECHO_TEST_CONNECTOR_ERN = "ern://echo:test:1";
    private Electrica instance;
    private EchoTestV1 echoTest;

    @BeforeAll
    void setUp() {
        init();
        UserDto user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        ConnectorDto connectorDto = getEchoConnector();
        contextHolder.setTokenForUser(user.getEmail());
        AccessKeyDto accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        ConnectionDto connectionDto = createConnection(getCurrTimeAsString(), connectorDto, accessKey.getId());
        Connector sdkConnector = instance.connector(connectorDto.getErn());
        Connection connection = sdkConnector.connection(connectionDto.getName());
        echoTest = new EchoTestV1(connection);
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
    void testPing() throws Exception {
        echoTest.ping();
    }

    @Test
    void testPingWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.ping(Boolean.TRUE);
        });
    }

    @Test
    void testPingWithTimeout() throws Exception {
        echoTest.ping(60L, TimeUnit.SECONDS);
    }

    @Test
    void testPingWithTimeoutAndException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.ping(Boolean.TRUE, 60L, TimeUnit.SECONDS);
        });
    }

    @Test
    void testAsyncPing() throws Exception {
        AsyncResponseHandler voidHandler = new AsyncResponseHandler();
        echoTest.asyncPing(voidHandler);
        voidHandler.awaitResponse();
    }

    @Test
    void testAsyncPingWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            AsyncResponseHandler voidHandler = new AsyncResponseHandler();
            echoTest.asyncPing(Boolean.TRUE, voidHandler);
            voidHandler.awaitResponse();
        });
    }

    @Test
    void testEcho() throws Exception {
        String result = echoTest.echo(TEST_STRING);
        assertEquals(result, TEST_STRING);
    }

    @Test
    void testEchoWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.echo(TEST_STRING, Boolean.TRUE);
        });
    }

    @Test
    void testEchoWithTimeout() throws Exception {
        String result = echoTest.echo(TEST_STRING, 60L, TimeUnit.SECONDS);
        assertEquals(result, TEST_STRING);
    }

    @Test
    void testEchoWithExceptionAndTimeout() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.echo(TEST_STRING, Boolean.TRUE, 60L, TimeUnit.SECONDS);
        });
    }

    @Test
    void testEchoWithCallback() throws Exception {
        AsyncResponseHandler result = new AsyncResponseHandler();
        echoTest.asyncEcho(TEST_STRING, result);
        String out = result.awaitResponse();
        assertEquals(TEST_STRING, out);
    }

    @Test
    void testEchoWithCallbackException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            AsyncResponseHandler result = new AsyncResponseHandler();
            echoTest.asyncEcho(TEST_STRING, Boolean.TRUE, result);
            String out = result.awaitResponse();
            assertEquals(TEST_STRING, out);
        });
    }

    private ConnectorDto getEchoConnector() {
        return connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getErn(), ECHO_TEST_CONNECTOR_ERN))
                .findFirst().get();
    }

    private static class AsyncResponseHandler implements Callback {

        private final BlockingQueue<Object[]> responseQueue = new ArrayBlockingQueue<>(1);

        @Override
        public void onResponse(Object result) {
            responseQueue.add(new Object[]{result});
        }

        @Override
        public void onFailure(IntegrationException exception) {
            responseQueue.add(new Object[]{exception});
        }

        @SneakyThrows
        private String awaitResponse() {
            Object[] responseContainer = responseQueue.poll(60, TimeUnit.SECONDS);
            if (responseContainer == null) {
                throw new TimeoutException();
            }
            Object o = responseContainer[0];
            if (o instanceof IntegrationException) {
                throw (IntegrationException) o;
            }
            return (String) o;
        }
    }
}
