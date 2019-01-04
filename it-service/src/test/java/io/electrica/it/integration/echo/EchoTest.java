package io.electrica.it.integration.echo;

import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
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
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class EchoTest extends BaseIT {

    private static final String TEST_STRING = "Hello";
    private static final String ECHO_TEST_CONNECTOR_ERN = "ern://echo:test:1";
    private Electrica instance;
    private EchoTestV1 echoTest;

    @BeforeAll
    public void setUp() {
        init();
        UserDto user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        ConnectorDto connectorDto = getEchoConnector();
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl), fullAccessKeyDto.getKey());
        ConnectionDto connectionDto = createConnection(getCurrTimeAsString(), connectorDto, accessKey.getId());
        Connector sdkConnector = instance.connector(connectorDto.getErn());
        Connection connection = sdkConnector.connection(connectionDto.getName());
        echoTest = new EchoTestV1(connection);
    }

    @AfterAll
    public void tearDown() {
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail("Instance close with Exception:" + e.getMessage());
        }
    }

    @Test
    public void testPing() throws Exception {
        echoTest.ping();
    }

    @Test
    public void testPingWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.ping(Boolean.TRUE);
        });
    }

    @Test
    public void testPingWithTimeout() throws Exception {
        echoTest.ping(60L, TimeUnit.SECONDS);
    }

    @Test
    public void testPingWithTimeoutAndException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.ping(Boolean.TRUE, 60L, TimeUnit.SECONDS);
        });
    }

    @Test
    public void testAsyncPing() throws Exception {
        AsyncResponseHandler voidHandler = new AsyncResponseHandler();
        echoTest.asyncPing(voidHandler);
        voidHandler.awaitResponse(60L, TimeUnit.SECONDS);
    }

    @Test
    public void testAsyncPingWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            AsyncResponseHandler voidHandler = new AsyncResponseHandler();
            echoTest.asyncPing(Boolean.TRUE, voidHandler);
            voidHandler.awaitResponse(60L, TimeUnit.SECONDS);
        });
    }

    @Test
    public void testEcho() throws Exception {
        String result = echoTest.echo(TEST_STRING);
        assertEquals(result, TEST_STRING);
    }

    @Test
    public void testEchoWithException() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.echo(TEST_STRING, Boolean.TRUE);
        });
    }

    @Test
    public void testEchoWithTimeout() throws Exception {
        String result = echoTest.echo(TEST_STRING, 60L, TimeUnit.SECONDS);
        assertEquals(result, TEST_STRING);
    }

    @Test
    public void testEchoWithExceptionAndTimeout() {
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.echo(TEST_STRING, Boolean.TRUE, 60L, TimeUnit.SECONDS);
        });
    }

    @Test
    public void testEchoWithCallback() throws Exception {
        AsyncResponseHandler result = new AsyncResponseHandler();
        echoTest.asyncEcho(TEST_STRING, result);
        String out = result.awaitResponse(60L, TimeUnit.SECONDS);
        assertEquals(TEST_STRING, out);
    }

    @Test
    public void testEchoWithCallbackException() {
        AsyncResponseHandler result = new AsyncResponseHandler();
        Assertions.assertThrows(IntegrationException.class, () -> {
            echoTest.asyncEcho(TEST_STRING, Boolean.TRUE, result);
            String out = result.awaitResponse(60L, TimeUnit.SECONDS);
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
        private String awaitResponse(Long timeout, TimeUnit unit) {
            Object[] responseContainer = responseQueue.poll(timeout, unit);
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
