package io.electrica.websocket.context;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.helper.AuthorityConstants;
import io.electrica.user.feign.AccessKeyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

import static io.electrica.common.helper.CollectionUtils.nullToFalse;

@Slf4j
@Component
public class SdkInstanceContextHandshakeInterceptor implements HandshakeInterceptor {

    public static final String INSTANCE_CONTEXT_ATTRIBUTE = "x-electrica-sdk-instance-context";

    private static final String INSTANCE_ID_HEADER = "x-electrica-sdk-instance-id";
    private static final String INSTANCE_NAME_HEADER = "x-electrica-sdk-instance-name";
    private static final String INSTANCE_START_CLIENT_TIME_HEADER = "x-electrica-sdk-instance-ws-session-start-time";

    private final IdentityContextHolder identityContextHolder;
    private final AccessKeyClient accessKeyClient;

    @Inject
    public SdkInstanceContextHandshakeInterceptor(
            IdentityContextHolder identityContextHolder,
            AccessKeyClient accessKeyClient
    ) {
        this.identityContextHolder = identityContextHolder;
        this.accessKeyClient = accessKeyClient;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        Identity identity = identityContextHolder.getIdentity();
        if (identity == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        // Check for SDK scope
        if (!identity.getOauthScopes().contains(AuthorityConstants.SDK_SCOPE)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        // Validate if access key hasn't been revoked
        if (!isAccessKeyValid()) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        // Get instance info
        HttpHeaders headers = request.getHeaders();
        String instanceIdString = headers.getFirst(INSTANCE_ID_HEADER);
        if (instanceIdString == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        UUID instanceId = UUID.fromString(instanceIdString);

        String instanceName = headers.getFirst(INSTANCE_NAME_HEADER);
        if (instanceName == null || instanceName.isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        ZonedDateTime instanceStartClientTime = extractStartClientTime(instanceId, headers);
        if (instanceStartClientTime == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // Set sdk instance context to WebSocket session attributes
        SdkInstanceContext context = new SdkInstanceContext(instanceId, instanceName,
                identity, instanceStartClientTime);
        attributes.put(INSTANCE_CONTEXT_ATTRIBUTE, context);

        return true;
    }

    @Nullable
    private ZonedDateTime extractStartClientTime(UUID instanceId, HttpHeaders headers) {
        ZonedDateTime instanceStartClientTime = null;
        String instanceStartClientTimeStr = headers.getFirst(INSTANCE_START_CLIENT_TIME_HEADER);
        if (instanceStartClientTimeStr != null && !instanceStartClientTimeStr.isEmpty()) {
            try {
                instanceStartClientTime = ZonedDateTime.parse(instanceStartClientTimeStr);
            } catch (DateTimeParseException e) {
                log.error("Can't parse instance start client time {} for instanceId {}",
                        instanceStartClientTime, instanceId);
            }
        }
        return instanceStartClientTime;
    }

    private boolean isAccessKeyValid() {
        Boolean result = accessKeyClient.validateMyAccessKey().getBody();
        return nullToFalse(result);
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // noop
    }
}
