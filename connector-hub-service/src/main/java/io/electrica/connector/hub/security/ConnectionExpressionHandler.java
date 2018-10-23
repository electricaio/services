package io.electrica.connector.hub.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.repository.ConnectionRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

public class ConnectionExpressionHandler {

    private final ConnectionRepository connectionRepository;
    private final Authentication authentication;
    private final MethodInvocation mi;

    public ConnectionExpressionHandler(ConnectionRepository connectionRepository,
                                       Authentication authentication,
                                       MethodInvocation mi) {
        this.connectionRepository = connectionRepository;
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

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final ConnectionRepository connectionRepository;

        public Factory(ConnectionRepository connectionRepository) {
            this.connectionRepository = connectionRepository;
        }

        @Override
        public String getNamespace() {
            return "connection";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new ConnectionExpressionHandler(connectionRepository, authentication, mi);
        }
    }

}
