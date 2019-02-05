package io.electrica.metric.common.mq.instance.session;

import io.electrica.metric.common.mq.MetricSender;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionClosedEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionEstablishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.CLOSED_ROUTING_KEY;
import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.ESTABLISHED_ROUTING_KEY;

@Slf4j
@Component
public class InstanceSessionEventSender {
    private final MetricSender metricSender;

    @Inject
    public InstanceSessionEventSender(MetricSender metricSender) {
        this.metricSender = metricSender;
    }

    public void sendEstablished(UUID id, String name, ZonedDateTime startedClientTime, long userId,
                                long organizationId, long accessKeyId) {
        metricSender.send(ESTABLISHED_ROUTING_KEY, InstanceConnectionEstablishedEvent.of(
                id, name, startedClientTime, userId, organizationId, accessKeyId
        ));
    }

    public void sendClosed(UUID id, String name, ZonedDateTime startedClientTime, int closedCode) {
        metricSender.send(CLOSED_ROUTING_KEY,
                InstanceConnectionClosedEvent.of(id, name, startedClientTime, closedCode));
    }
}
