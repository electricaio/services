package io.electrica.webhook.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.webhook.dto.WebhookDto;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Register webhook namespace and support methods for Pre and Post authentication.
 */
public class WebhookExpressionMethods {

    private final Authentication authentication;
    private final ConnectionClient connectionClient;

    public WebhookExpressionMethods(Authentication authentication, ConnectionClient connectionClient) {
        this.authentication = authentication;
        this.connectionClient = connectionClient;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public Boolean canUserAccess(Long connectionId) {
        return connectionClient.validate(connectionId).getBody();
    }

    public Boolean allWebhooksWithinCurrentUser(List<WebhookDto> webhooks) {
        return webhooks.stream()
                .allMatch(w -> w.getUserId().equals(getIdentity().getUserId()));
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
            return new WebhookExpressionMethods(authentication, connectionClient);
        }
    }

}

