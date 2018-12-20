package io.electrica.common.mq.webhook;

import org.springframework.amqp.core.*;

import java.util.UUID;

public class WebhookResultMessages {

    public static final String EXCHANGE = "webhook-message-result";

    private static final String QUEUE_TEMPLATE = "webhook-message-result-%s";
    private static final String ROUTING_KEY_TEMPLATE = "webhook-message-result.%s";

    private WebhookResultMessages() {
    }

    public static Exchange newExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE)
                .durable(true)
                .build();
    }

    public static String queueName(UUID serviceInstanceId) {
        return String.format(QUEUE_TEMPLATE, serviceInstanceId);
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

    public static String routingKey(UUID serviceInstanceId) {
        return String.format(ROUTING_KEY_TEMPLATE, serviceInstanceId);
    }

    public static Binding newBinding(Queue queue, Exchange exchange, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey)
                .noargs();
    }

}
