package io.electrica.common.mq.webhook;

import org.springframework.amqp.core.*;

public class WebhookMessages {

    public static final String EXCHANGE = "webhooks";

    public static final String QUEUE_TEMPLATE = "webhook-%s-%s-%s";

    public static final String ROUTING_KEY_TEMPLATE = "webhook.%s.%s.%s";
    public static final String ALL_BINDING_KEY = String.format(ROUTING_KEY_TEMPLATE, "*", "*", "*");

    private WebhookMessages() {
    }

    public static Exchange newExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE)
                .durable(true)
                .build();
    }

    public static Queue newQueue(String name, Long messageTtl, Long queueTtl) {
        // ToDo mb use lazy queue for this one?
        // Set the queue into lazy mode, keeping as many messages as possible on disk to reduce RAM usage; if not set,
        // the queue will keep an in-memory cache to deliver messages as fast as possible.
        // (Sets the "x-queue-mode" argument.)
        return QueueBuilder.durable(name)
                .withArgument("x-message-ttl", messageTtl)
                .withArgument("x-expires", queueTtl)
                .build();
    }

    public static String queueName(Long organizationId, Long userId, Long accessKeyId) {
        return String.format(QUEUE_TEMPLATE, organizationId, userId, accessKeyId);
    }

    public static String routingKey(Long organizationId, Long userId, Long accessKeyId) {
        return String.format(ROUTING_KEY_TEMPLATE, organizationId, userId, accessKeyId);
    }

    public static Binding newBinding(Queue queue, Exchange exchange, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey)
                .noargs();
    }

}
