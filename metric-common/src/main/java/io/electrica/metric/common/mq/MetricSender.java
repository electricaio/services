package io.electrica.metric.common.mq;

import com.google.common.collect.ImmutableSet;
import io.electrica.common.mq.PersistentMessagePostProcessor;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionClosedEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionEstablishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.Set;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;

@Slf4j
@Component
public class MetricSender {
    private static final Set<Class<? extends MetricEvent>> INSTANCE_SESSION_EVENTS = ImmutableSet.of(
            InstanceConnectionEstablishedEvent.class,
            InstanceConnectionClosedEvent.class
    );

    private final RabbitTemplate rabbitTemplate;
    private final boolean metricsEnabled;

    @Inject
    public MetricSender(RabbitTemplate rabbitTemplate,
                        @Value("${common.metric.invocation.enabled}") boolean metricsEnabled) {
        this.rabbitTemplate = rabbitTemplate;
        this.metricsEnabled = metricsEnabled;
    }

    public void send(String routingKey, MetricEvent event) {
        //It is hard to track instance sessions state via logs and events are relatively rare
        //so they should be sent and stored even though if other metrics are turned off
        if (INSTANCE_SESSION_EVENTS.contains(event.getClass()) || metricsEnabled) {
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
}
