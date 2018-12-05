package io.electrica.websocket.context;

import io.electrica.common.context.IdentityImpl;
import io.electrica.common.helper.AuthorityConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class SdkInstanceContextHandshakeInterceptor implements HandshakeInterceptor {

    public static final String INSTANCE_CONTEXT_ATTRIBUTE = "x-electrica-sdk-instance-context";
    private static final String INSTANCE_ID_HEADER = "x-electrica-sdk-instance-id";

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {
        Principal principal = request.getPrincipal();
        if (!(principal instanceof OAuth2Authentication)) {
            throw new IllegalStateException("User must be oauth2 authenticated");
        }
        OAuth2Authentication authentication = (OAuth2Authentication) principal;

        // Check permissions
        Set<String> scope = authentication.getOAuth2Request().getScope();
        if (!scope.contains(AuthorityConstants.SDK_SCOPE)) {
            // User must have SDK scope
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

        // Set sdk instance context to WebSocket session attributes
        SdkInstanceContext context = new SdkInstanceContext(instanceId, new IdentityImpl(authentication));
        attributes.put(INSTANCE_CONTEXT_ATTRIBUTE, context);

        return true;
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
