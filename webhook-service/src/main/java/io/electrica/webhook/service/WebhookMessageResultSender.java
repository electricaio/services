package io.electrica.webhook.service;

import io.electrica.common.EnvironmentType;
import io.electrica.common.mq.PersistentMessagePostProcessor;
import io.electrica.common.mq.webhook.WebhookResultMessages;
import io.electrica.webhook.dto.MessageResultDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.UUID;

import static io.electrica.webhook.config.WebhookConfig.INSTANCE_ID_QUALIFIER;

@Component
public class WebhookMessageResultSender {

    private final UUID serviceInstanceId;
    private final RabbitTemplate rabbitTemplate;
    private final boolean deliveryOptimizationEnabled;
    private final WebhookMessageResultDispatcher webhookMessageResultDispatcher;

    @Inject
    public WebhookMessageResultSender(
            @Named(INSTANCE_ID_QUALIFIER) UUID serviceInstanceId,
            RabbitTemplate rabbitTemplate,
            EnvironmentType environmentType,
            WebhookMessageResultDispatcher webhookMessageResultDispatcher
    ) {
        this.serviceInstanceId = serviceInstanceId;
        this.rabbitTemplate = rabbitTemplate;
        this.deliveryOptimizationEnabled = !environmentType.isSafe();
        this.webhookMessageResultDispatcher = webhookMessageResultDispatcher;
    }

    public void send(MessageResultDto messageResult) {
        UUID webhookServiceId = messageResult.getWebhookServiceId();
        if (handleWithCurrentInstance(webhookServiceId)) {
            // don't send message to the queue if it for the same instance
            webhookMessageResultDispatcher.handle(messageResult);
        } else {
            String routingKey = WebhookResultMessages.routingKey(webhookServiceId);
            rabbitTemplate.convertAndSend(
                    WebhookResultMessages.EXCHANGE,
                    routingKey,
                    messageResult,
                    PersistentMessagePostProcessor.INSTANCE
            );
        }
    }

    private boolean handleWithCurrentInstance(UUID webhookServiceId) {
        return deliveryOptimizationEnabled && Objects.equals(this.serviceInstanceId, webhookServiceId);
    }
}
