package io.electrica.webhook.service;

import io.electrica.webhook.model.Webhook;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Properties;

import static io.electrica.common.mq.WebhookMessages.*;

@Component
public class WebhookQueueDispatcher {

    private final AmqpAdmin amqpAdmin;
    private final Exchange webhooksExchange;

    private final Long messageTtl;
    private final Long queueTtl;

    @Inject
    public WebhookQueueDispatcher(
            AmqpAdmin amqpAdmin,
            Exchange webhooksExchange,
            @Value("${webhook.message-ttl}") Long messageTtl,
            @Value("${webhook.queue-ttl}") Long queueTtl
    ) {
        this.amqpAdmin = amqpAdmin;
        this.webhooksExchange = webhooksExchange;
        this.messageTtl = messageTtl;
        this.queueTtl = queueTtl;
    }

    public void createQueueIfAbsent(Webhook webhook) {
        String queueName = queueName(
                webhook.getOrganizationId(),
                webhook.getUserId(),
                webhook.getAccessKeyId()
        );
        Properties properties = amqpAdmin.getQueueProperties(queueName);
        if (properties == null) {
            Queue queue = newQueue(queueName, messageTtl, queueTtl);
            amqpAdmin.declareQueue(queue);
            String routingKey = routingKey(
                    webhook.getOrganizationId(),
                    webhook.getUserId(),
                    webhook.getAccessKeyId()
            );
            Binding binding = newBinding(queue, webhooksExchange, routingKey);
            amqpAdmin.declareBinding(binding);
        }
    }

}
