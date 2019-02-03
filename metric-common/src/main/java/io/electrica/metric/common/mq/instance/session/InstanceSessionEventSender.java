package io.electrica.metric.common.mq.instance.session;

import io.electrica.common.mq.PersistentMessagePostProcessor;
import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionClosedEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionEstablishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.CLOSED_ROUTING_KEY;
import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.ESTABLISHED_ROUTING_KEY;

@Slf4j
@Component
public class InstanceSessionEventSender {
    private final RabbitTemplate rabbitTemplate;

    @Inject
    public InstanceSessionEventSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEstablished(UUID id, String name, ZonedDateTime startedClientTime, long userId,
                                long organizationId, long accessKeyId) {
        send(ESTABLISHED_ROUTING_KEY, InstanceConnectionEstablishedEvent.of(
                id, name, startedClientTime, userId, organizationId, accessKeyId
        ));
    }

    public void sendClosed(UUID id, String name, ZonedDateTime startedClientTime, int closedCode) {
        send(CLOSED_ROUTING_KEY, InstanceConnectionClosedEvent.of(id, name, startedClientTime, closedCode));
    }

    private void send(String routingKey, MetricEvent event) {
        rabbitTemplate.convertAndSend(
                InstanceSessionMetrics.EXCHANGE,
                routingKey,
                event,
                PersistentMessagePostProcessor.INSTANCE
        );
    }
}
