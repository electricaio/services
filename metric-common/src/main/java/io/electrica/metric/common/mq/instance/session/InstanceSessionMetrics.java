package io.electrica.metric.common.mq.instance.session;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;

public class InstanceSessionMetrics {
    public static final String ROUTING_PREFIX = METRIC_EXCHANGE + ".isntance-session.";
    public static final String ESTABLISHED_ROUTING_KEY = ROUTING_PREFIX + "established";
    public static final String CLOSED_ROUTING_KEY = ROUTING_PREFIX + "closed";

    private InstanceSessionMetrics() {}
}
