package io.electrica.webhook.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.mq.PersistentMessagePostProcessor;
import io.electrica.common.mq.webhook.WebhookMessageQueueDispatcher;
import io.electrica.common.mq.webhook.WebhookMessages;
import io.electrica.webhook.message.WebhookMessage;
import io.electrica.webhook.model.Webhook;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

import static io.electrica.webhook.config.WebhookConfig.INSTANCE_ID_QUALIFIER;
import static io.electrica.webhook.helper.SignHelper.checkSign;

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

    private WebhookMessage buildMessage(Webhook webhook, String payload, String contentType,
                                        String expectedContentType, boolean expectedResult) {
        return new WebhookMessage(
                UUID.randomUUID(),
                webhook.getId(),
                serviceInstanceId,
                webhook.getName(),
                webhook.getOrganizationId(),
                webhook.getUserId(),
                webhook.getAccessKeyId(),
                webhook.getIsPublic(),
                webhook.getScope(),
                webhook.getConnectorId(),
                webhook.getConnectorErn(),
                webhook.getConnectionId(),
                webhook.getProperties(),
                expectedResult,
                expectedContentType,
                payload,
                contentType
        );
    }

    public UUID send(
            UUID webhookId,
            String payload,
            String contentType,
            @Nullable String expectedContentType,
            boolean expectedResult,
            boolean isPublic,
            @Nullable String sign
    ) {
        Webhook webhook = webhookService.findById(webhookId);
        if (isPublic && (!webhook.getIsPublic() || !checkSign(webhook, sign))) {
            throw new BadRequestServiceException("Webhook not found");
        }

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

        WebhookMessage message = buildMessage(webhook, payload, contentType, expectedContentType, expectedResult);
        rabbitTemplate.convertAndSend(
                WebhookMessages.EXCHANGE,
                routingKey,
                message,
                PersistentMessagePostProcessor.INSTANCE
        );
        return message.getId();
    }
}
