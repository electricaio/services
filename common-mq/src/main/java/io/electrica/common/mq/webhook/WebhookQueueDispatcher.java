package io.electrica.common.mq.webhook;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Properties;

import static io.electrica.common.mq.webhook.WebhookMessages.*;

@Lazy
@Component
public class WebhookQueueDispatcher {

    private final AmqpAdmin amqpAdmin;

    private final Long messageTtl;
    private final Long queueTtl;

    private Exchange exchange;

    @Inject
    public WebhookQueueDispatcher(
            AmqpAdmin amqpAdmin,
            @Value("${mq.webhook.message-ttl}") Long messageTtl,
            @Value("${mq.webhook.queue-ttl}") Long queueTtl
    ) {
        this.amqpAdmin = amqpAdmin;
        this.messageTtl = messageTtl;
        this.queueTtl = queueTtl;
    }

    @PostConstruct
    public void init() {
        exchange = WebhookMessages.newExchange();
        amqpAdmin.declareExchange(exchange);
    }

    public String createQueueIfAbsent(Long organizationId, Long userId, Long accessKeyId) {
        String queueName = queueName(organizationId, userId, accessKeyId);
        Properties properties = amqpAdmin.getQueueProperties(queueName);
        if (properties == null) {
            Queue queue = newQueue(queueName, messageTtl, queueTtl);
            amqpAdmin.declareQueue(queue);
            String routingKey = routingKey(organizationId, userId, accessKeyId);
            Binding binding = newBinding(queue, exchange, routingKey);
            amqpAdmin.declareBinding(binding);
        }
        return queueName;
    }

}
