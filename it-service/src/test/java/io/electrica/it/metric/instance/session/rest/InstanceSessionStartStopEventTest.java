package io.electrica.it.metric.instance.session.rest;

import io.electrica.common.security.RoleType;
import io.electrica.it.BaseIT;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import io.electrica.metric.instance.session.feign.InstanceSessionClient;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.sdk.java8.api.Electrica;
import io.electrica.sdk.java8.core.SingleInstanceHttpModule;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.electrica.sdk.java8.core.WebSocketHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class InstanceSessionStartStopEventTest extends BaseIT {
    @Inject
    private InstanceSessionClient instanceSessionClient;

    private FullAccessKeyDto fullAccessKeyDto;
    private OkHttpClient okHttpClient;

    @BeforeAll
    public void init() {
        super.init();
        UserDto user = createUser(ORG_HACKER_RANK, RoleType.SuperAdmin);
        contextHolder.setTokenForUser(user.getEmail());
        AccessKeyDto accessKey = createAccessKey(user.getId(), "StartStopTest" + getCurrTimeAsString());
        fullAccessKeyDto = accessKeyClient.getAccessKey(accessKey.getId()).getBody();
        contextHolder.setTokenForAdmin();
        okHttpClient = new OkHttpClient.Builder()
                .pingInterval(500, TimeUnit.MILLISECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    @Test
    public void testRunningStopped() throws Exception {
        Electrica instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl),
                fullAccessKeyDto.getKey());
        checkState(instance.getInstanceId(), SessionState.Running);
        instance.close();
        checkState(instance.getInstanceId(), SessionState.Stopped);
    }

    @Test
    public void testRunningExpiredRunningStopped() throws Exception {
        UUID instanceId = UUID.randomUUID();
        TestWebSocket webSocket = newWebSocket(instanceId);
        checkState(instanceId, SessionState.Running);
        webSocket.closeWithCode(1002);
        checkState(instanceId, SessionState.Expired);
        webSocket = newWebSocket(instanceId);
        checkState(instanceId, SessionState.Running);
        webSocket.closeWithCode(1000);
        checkState(instanceId, SessionState.Stopped);
    }

    private void checkState(UUID instanceId, SessionState expectedState)
            throws InterruptedException {
        SessionState sessionState = null;
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            sessionState = instanceSessionClient.feignGetInstanceSessions(0, 1, null,
                    LocalDateTime.now(), instanceId.toString(), null, null,
                    null, null).stream()
                    .filter(is -> is.getId().equals(instanceId))
                    .findFirst()
                    .map(InstanceSessionDto::getSessionState)
                    .orElse(null);
            if (expectedState == sessionState) {
                break;
            }
        }
        assertEquals(expectedState, sessionState);
    }

    private TestWebSocket newWebSocket(UUID instanceId) throws InterruptedException {
        CountDownLatch opened = new CountDownLatch(1);
        CountDownLatch closed = new CountDownLatch(1);

        WebSocket webSocket = okHttpClient.newWebSocket(buildRequest(instanceId), new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                opened.countDown();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                closed.countDown();
            }
        });

        opened.await(5, TimeUnit.SECONDS);

        return new TestWebSocket(closed, webSocket);
    }

    @AllArgsConstructor
    private static class TestWebSocket {
        private CountDownLatch closed;
        private WebSocket webSocket;

        public void closeWithCode(int code) throws InterruptedException {
            webSocket.close(code, "");
            closed.await(5, TimeUnit.SECONDS);
            webSocket.cancel();
        }
    }

    private Request buildRequest(UUID instanceId) {
        return new Request.Builder()
                .url(buildEndpointUrl(invokerServiceUrl))
                .header("Authorization", "Bearer " + fullAccessKeyDto.getKey())
                .header(INSTANCE_ID_HEADER, instanceId.toString())
                .header(INSTANCE_NAME_HEADER, instanceId.toString())
                .header(INSTANCE_START_CLIENT_TIME_HEADER, ZonedDateTime.now().toString())
                .build();
    }

    private String buildEndpointUrl(String apiUrl) {
        String url = apiUrl.contains("https") ?
                apiUrl.replaceFirst("https", "wss") :
                apiUrl.replaceFirst("http", "ws");
        return url + WEBSOCKETS_PATH;
    }
}
