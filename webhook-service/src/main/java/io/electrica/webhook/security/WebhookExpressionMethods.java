package io.electrica.webhook.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.webhook.dto.WebhookDto;
import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.service.WebhookService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Register webhook namespace and support methods for Pre and Post authentication.
 */
public class WebhookExpressionMethods {

    private final Authentication authentication;
    private final ConnectionClient connectionClient;
    private final WebhookService webhookService;


    public WebhookExpressionMethods(Authentication authentication, ConnectionClient connectionClient,
                                    WebhookService webhookService) {
        this.authentication = authentication;
        this.connectionClient = connectionClient;
        this.webhookService = webhookService;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public Boolean canUserAccess(Long connectionId) {
        return connectionClient.validate(connectionId).getBody();
    }

    public Boolean allWebhooksBelongsCurrentUser(List<WebhookDto> webhooks) {
        return webhooks.stream()
                .allMatch(w -> w.getUserId().equals(getIdentity().getUserId()));
    }

    public Boolean webhookBelongsCurrentUser(UUID id) {
        Webhook webhook = webhookService.findById(id);
        return webhook.getUserId().equals(getIdentity().getUserId());
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final ConnectionClient connectionClient;
        private final WebhookService webhookService;

        public Factory(ConnectionClient connectionClient, WebhookService webhookService) {
            this.connectionClient = connectionClient;
            this.webhookService = webhookService;
        }

        @Override
        public String getNamespace() {
            return "webhook";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new WebhookExpressionMethods(authentication, connectionClient, webhookService);
        }
    }

}

