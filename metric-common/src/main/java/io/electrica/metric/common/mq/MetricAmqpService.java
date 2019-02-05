package io.electrica.metric.common.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.common.condition.NotTestCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_DEAD_LETTER_EXCHANGE_QUALIFIER;
import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE_QUALIFIER;

@Slf4j
@Component
@Conditional(NotTestCondition.class)
public class MetricAmqpService {
    private static final int MAX_RETRY = 3;
    private static final String DEAD_LETTER_QUEUE_POSTFIX = "-dlq";

    private final Exchange metricExchange;
    private final Exchange deadLetterMetricExchange;
    private final ConnectionFactory connectionFactory;
    private final Long ttlDlqMessage;
    private final AmqpAdmin amqpAdmin;
    private final ObjectMapper objectMapper;

    public MetricAmqpService(@Named(METRIC_EXCHANGE_QUALIFIER) Exchange metricExchange,
                             @Named(METRIC_DEAD_LETTER_EXCHANGE_QUALIFIER) Exchange deadLetterMetricExchange,
                             ConnectionFactory connectionFactory,
                             AmqpAdmin amqpAdmin,
                             @Value("${mq.metric.message.dead-letter-queue.message-ttl}") Long ttlDlqMessage,
                             ObjectMapper objectMapper) {
        this.metricExchange = metricExchange;
        this.deadLetterMetricExchange = deadLetterMetricExchange;
        this.connectionFactory = connectionFactory;
        this.amqpAdmin = amqpAdmin;
        this.ttlDlqMessage = ttlDlqMessage;
        this.objectMapper = objectMapper;
    }

    /**
     * Declare metric queue and bind to metric exchange with routing key.
     * For each queue DLQ so the queue will refer to DQL as dead-letter-exchange
     * and DQL will refer to the queue as dead-letter-exchange.
     *
     * When listener throw exception message will be not acknowledged and sent to DLQ.
     * DLQ will resent back to this queue messages after {mq.metric.message.dead-letter-queue.message-ttl} delay
     * If a message was rejected 3 times error will be written in log and message acknowledged
     */
    public Queue declareAndBindQueue(String queueName, String routingKey) {
        Queue dlq = QueueBuilder.durable(queueName + DEAD_LETTER_QUEUE_POSTFIX)
                .withArgument("x-dead-letter-exchange", metricExchange.getName())
                .withArgument("x-dead-letter-routing-key", routingKey)
                .withArgument("x-message-ttl", ttlDlqMessage)
                .build();
        amqpAdmin.declareQueue(dlq);
        Binding dlqBinding = BindingBuilder.bind(dlq)
                .to(deadLetterMetricExchange)
                .with(routingKey)
                .noargs();
        amqpAdmin.declareBinding(dlqBinding);

        Queue queue = QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", deadLetterMetricExchange.getName())
                .withArgument("x-dead-letter-routing-key", routingKey)
                .build();
        amqpAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue)
                .to(metricExchange)
                .with(routingKey)
                .noargs();
        amqpAdmin.declareBinding(binding);

        return queue;
    }

    public <T extends MetricEvent> MessageListenerContainer buildListener(String queueName, Class<T> dtoClass,
                                                      BiConsumer<Message, T> listener) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setQueueNames(queueName);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setDefaultRequeueRejected(false);
        container.setChannelAwareMessageListener(((message, channel) -> {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            try {
                T eventDto = objectMapper.readValue(message.getBody(), dtoClass);
                listener.accept(message, eventDto);
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                long retries = ((List<Map<String, Object>>) headers
                            .getOrDefault("x-death", Collections.emptyList())).stream()
                            .findFirst()
                            .map(m -> (Long) m.get("count"))
                            .orElse(0L);
                if (retries < MAX_RETRY) {
                    channel.basicNack(deliveryTag, false, false);
                } else {
                    log.error("Event message retry limit reached for listener:{} message:{}",
                            listener.getClass().getName(), message, e);
                    channel.basicAck(deliveryTag, false);
                }
            }
        }));
        return container;
    }
}
