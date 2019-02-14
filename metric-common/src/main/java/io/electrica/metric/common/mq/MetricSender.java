package io.electrica.metric.common.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import io.electrica.common.context.IdentityContextHolder;
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
    private final boolean loggingEnabled;
    private final ObjectMapper objectMapper;
    private final IdentityContextHolder identityContextHolder;

    @Inject
    public MetricSender(RabbitTemplate rabbitTemplate,
                        @Value("${common.metric.invocation.enabled}") boolean metricsEnabled,
                        @Value("${common.request.metric.logging.enabled}") boolean loggingEnabled,
                        ObjectMapper objectMapper,
                        IdentityContextHolder identityContextHolder) {
        this.rabbitTemplate = rabbitTemplate;
        this.metricsEnabled = metricsEnabled;
        this.loggingEnabled = loggingEnabled;
        this.objectMapper = objectMapper;
        this.identityContextHolder = identityContextHolder;
    }

    public void send(String routingKey, MetricEvent event) {
        if (loggingEnabled) {
            try {
                String strMetricEvent = objectMapper.writeValueAsString(event);
                identityContextHolder.logForUserIfNoContext(
                        event.getOrganizationId(),
                        event.getUserId(),
                        event.getAccessKeyId(),
                        () -> log.debug("Metric event {}", strMetricEvent)
                );
            } catch (JsonProcessingException e) {
                log.debug("Can't serialize metric event", e);
            }
        }

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
