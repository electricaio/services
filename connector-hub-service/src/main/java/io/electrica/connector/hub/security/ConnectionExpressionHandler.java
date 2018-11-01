package io.electrica.connector.hub.security;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.repository.ConnectionRepository;
import io.electrica.user.feign.AccessKeyClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

public class ConnectionExpressionHandler {

    private final ConnectionRepository connectionRepository;
    private final AccessKeyClient accessKeyClient;
    private final Authentication authentication;
    private final MethodInvocation mi;

    public ConnectionExpressionHandler(ConnectionRepository connectionRepository, AccessKeyClient accessKeyClient,
                                       Authentication authentication, MethodInvocation mi) {
        this.connectionRepository = connectionRepository;
        this.accessKeyClient = accessKeyClient;
        this.authentication = authentication;
        this.mi = mi;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public boolean canUserAccess(Long connectionId) {
        final Long userId = getIdentity().getUserId();
        return connectionRepository.exists(connectionId, userId);
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    public boolean accessKeyBelongsUser(Long accessKey) {
        return accessKeyClient.validateAccessKey(accessKey).getBody();
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final ConnectionRepository connectionRepository;
        private final AccessKeyClient accessKeyClient;

        public Factory(ConnectionRepository connectionRepository, AccessKeyClient accessKeyClient) {
            this.connectionRepository = connectionRepository;
            this.accessKeyClient = accessKeyClient;
        }

        @Override
        public String getNamespace() {
            return "connection";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new ConnectionExpressionHandler(connectionRepository, accessKeyClient, authentication, mi);
        }
    }

}
