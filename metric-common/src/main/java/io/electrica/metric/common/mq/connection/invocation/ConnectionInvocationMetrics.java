package io.electrica.metric.common.mq.connection.invocation;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;

public class ConnectionInvocationMetrics {
    public static final String ROUTING_PREFIX = METRIC_EXCHANGE + ".connection-invocation.";
    public static final String CONNECTION_INVOCATION_ROUTING_KEY = ROUTING_PREFIX + "invoked";
    public static final String CONNECTION_INVOCATION_RESULT_ROUTING_KEY = ROUTING_PREFIX + "result";

    private ConnectionInvocationMetrics() {}
}
