package io.electrica.metric.common.mq.webhook.invocation;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;

public class WebhookInvcoationMetrics {
    public static final String ROUTING_PREFIX = METRIC_EXCHANGE + ".webhook-invocation.";
    public static final String WEBHOOK_INVOCATION_ROUTING_KEY = ROUTING_PREFIX + "invoked";
    public static final String WEBHOOK_INVOCATION_RESULT_ROUTING_KEY = ROUTING_PREFIX + "result";
    public static final String WEBHOOK_INVOCATION_ERROR_ROUTING_KEY = ROUTING_PREFIX + "error";

    private WebhookInvcoationMetrics() {}
}
