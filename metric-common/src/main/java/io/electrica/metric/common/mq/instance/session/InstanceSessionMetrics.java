package io.electrica.metric.common.mq.instance.session;

import com.google.common.collect.ImmutableMap;
import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionClosedEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionEstablishedEvent;

import java.util.Map;

public class InstanceSessionMetrics {
    public static final String EXCHANGE = "metric-message.instance-session";
    public static final String QUEUE_NAME = "metric-message.instance-session";
    public static final String ROUTING_PREFIX = "metric-message.isntance-session.";
    public static final String QUEUE_ROUTING_KEY = ROUTING_PREFIX + "*";
    public static final String ESTABLISHED_ROUTING_KEY = ROUTING_PREFIX + "established";
    public static final String CLOSED_ROUTING_KEY = ROUTING_PREFIX + "closed";
    public static final Map<String, Class<? extends MetricEvent>> KEY_TO_TYPE = ImmutableMap.of(
            ESTABLISHED_ROUTING_KEY, InstanceConnectionEstablishedEvent.class,
            CLOSED_ROUTING_KEY, InstanceConnectionClosedEvent.class
    );

    private InstanceSessionMetrics() {
    }
}