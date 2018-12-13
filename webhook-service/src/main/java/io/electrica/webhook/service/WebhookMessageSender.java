package io.electrica.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dozermapper.core.Mapper;
import io.electrica.common.mq.webhook.WebhookMessages;
import io.electrica.common.mq.webhook.WebhookQueueDispatcher;
import io.electrica.webhook.message.WebhookMessage;
import io.electrica.webhook.model.Webhook;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component
public class WebhookMessageSender {

    private final Mapper mapper;
    private final WebhookService webhookService;
    private final RabbitTemplate rabbitTemplate;
    private final WebhookQueueDispatcher webhookQueueDispatcher;
    private final MessagePostProcessor messagePostProcessor = message -> {
        MessageProperties properties = message.getMessageProperties();
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        return message;
    };

    @Inject
    public WebhookMessageSender(
            Mapper mapper,
            WebhookService webhookService,
            RabbitTemplate rabbitTemplate,
            WebhookQueueDispatcher webhookQueueDispatcher
    ) {
        this.mapper = mapper;
        this.webhookService = webhookService;
        this.rabbitTemplate = rabbitTemplate;
        this.webhookQueueDispatcher = webhookQueueDispatcher;
    }

    private WebhookMessage buildMessage(Webhook webhook, JsonNode payload, boolean expectedResult) {
        WebhookMessage message = mapper.map(webhook, WebhookMessage.class);
        message.setExpectedResult(expectedResult);
        message.setPayload(payload);
        return message;
    }

    public void send(UUID webhookId, JsonNode payload, boolean expectedResult) {
        Webhook webhook = webhookService.findById(webhookId);

        webhookQueueDispatcher.createQueueIfAbsent(
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
        rabbitTemplate.convertAndSend(WebhookMessages.EXCHANGE, routingKey, message, messagePostProcessor);
    }
}
