package io.electrica.webhook.security;

import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.service.WebhookService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Register webhook namespace and support methods for Pre and Post authentication.
 */
public class WebhookExpressionMethods extends CommonExpressionMethods {

    private final ConnectionClient connectionClient;
    private final WebhookService webhookService;

    public WebhookExpressionMethods(
            Authentication authentication,
            ConnectionClient connectionClient,
            WebhookService webhookService
    ) {
        super(authentication);
        this.connectionClient = connectionClient;
        this.webhookService = webhookService;
    }

    public Boolean connectionBelongsCurrentUser(Long connectionId) {
        return connectionClient.connectionBelongsCurrentUser(connectionId).getBody();
    }

    public Boolean connectionBelongsAccessKey(Long connectionId, Long accessKeyId) {
        ConnectionDto connection = connectionClient.get(connectionId).getBody();
        return connection != null && Objects.equals(accessKeyId, connection.getAccessKeyId());
    }

    public Boolean connectionWebhooksBelongsCurrentUser(List<ConnectionWebhookDto> webhooks) {
        return webhooks.stream()
                .allMatch(w -> isUser(w.getUserId()));
    }

    public Webhook getWebhook(UUID id) {
        return webhookService.findById(id);
    }

    public Boolean webhookBelongsCurrentUser(UUID id) {
        Webhook webhook = getWebhook(id);
        return isUser(webhook.getUserId());
    }

    public Boolean webhookBelongsCurrentAccessKey(UUID id) {
        Webhook webhook = getWebhook(id);
        return isUser(webhook.getUserId()) && isAccessKey(webhook.getAccessKeyId());
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

