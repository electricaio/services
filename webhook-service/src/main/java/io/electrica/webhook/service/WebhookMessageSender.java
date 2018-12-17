package io.electrica.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.common.mq.PersistentMessagePostProcessor;
import io.electrica.common.mq.webhook.WebhookMessageQueueDispatcher;
import io.electrica.common.mq.webhook.WebhookMessages;
import io.electrica.webhook.message.WebhookMessage;
import io.electrica.webhook.model.Webhook;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

import static io.electrica.webhook.config.WebhookConfig.INSTANCE_ID_QUALIFIER;

@Component
public class WebhookMessageSender {

    private final UUID serviceInstanceId;
    private final WebhookService webhookService;
    private final RabbitTemplate rabbitTemplate;
    private final WebhookMessageQueueDispatcher webhookMessageQueueDispatcher;

    @Inject
    public WebhookMessageSender(
            @Named(INSTANCE_ID_QUALIFIER) UUID serviceInstanceId,
            WebhookService webhookService,
            RabbitTemplate rabbitTemplate,
            WebhookMessageQueueDispatcher webhookMessageQueueDispatcher
    ) {
        this.serviceInstanceId = serviceInstanceId;
        this.webhookService = webhookService;
        this.rabbitTemplate = rabbitTemplate;
        this.webhookMessageQueueDispatcher = webhookMessageQueueDispatcher;
    }

    private WebhookMessage buildMessage(Webhook webhook, JsonNode payload, boolean expectedResult) {
        return new WebhookMessage(
                UUID.randomUUID(),
                webhook.getId(),
                serviceInstanceId,
                webhook.getName(),
                webhook.getOrganizationId(),
                webhook.getUserId(),
                webhook.getAccessKeyId(),
                webhook.getScope(),
                webhook.getConnectorId(),
                webhook.getConnectorErn(),
                webhook.getConnectionId(),
                webhook.getProperties(),
                expectedResult,
                payload
        );
    }

    public UUID send(UUID webhookId, JsonNode payload, boolean expectedResult) {
        Webhook webhook = webhookService.findById(webhookId);

        webhookMessageQueueDispatcher.createQueueIfAbsent(
                webhook.getOrganizationId(),
                webhook.getUserId(),
                webhook.getAccessKeyId()
        );

        String routingKey = WebhookMessages.routingKey(
                webhook.getOrganizationId(),
                webhook.getUserId(),
                webhook.getAccessKeyId()
        );

        WebhookMessage message = buildMessage(webhook, payload, expectedResult);
        rabbitTemplate.convertAndSend(
                WebhookMessages.EXCHANGE,
                routingKey,
                message,
                PersistentMessagePostProcessor.INSTANCE
        );
        return message.getId();
    }
}
