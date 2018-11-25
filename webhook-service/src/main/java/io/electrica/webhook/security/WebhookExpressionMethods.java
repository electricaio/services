package io.electrica.webhook.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.feign.ConnectionClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Register webhook namespace and support methods for Pre and Post authentication.
 */
public class WebhookExpressionMethods {

    private final ConnectionClient connectionClient;
    private final Authentication authentication;

    private WebhookExpressionMethods(
            ConnectionClient connectionClient,
            Authentication authentication
    ) {
        this.connectionClient = connectionClient;
        this.authentication = authentication;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public Boolean canUserAccess(Long connectionId) {
        return connectionClient.validate(connectionId).getBody();
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final ConnectionClient connectionClient;

        public Factory(ConnectionClient connectionClient) {
            this.connectionClient = connectionClient;
        }

        @Override
        public String getNamespace() {
            return "webhook";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new WebhookExpressionMethods(connectionClient, authentication);
        }
    }

}

