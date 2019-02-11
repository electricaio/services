package io.electrica.metric.common.mq;

import io.electrica.common.mq.PersistentMessagePostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;

@Slf4j
@Component
public class MetricSender {
    private final RabbitTemplate rabbitTemplate;

    @Inject
    public MetricSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String routingKey, MetricEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    METRIC_EXCHANGE,
                    routingKey,
                    event,
                    PersistentMessagePostProcessor.INSTANCE
            );
        } catch (Exception e) {
            log.error("Can't send {} metric event {}", routingKey, event, e);
        }
    }
}
