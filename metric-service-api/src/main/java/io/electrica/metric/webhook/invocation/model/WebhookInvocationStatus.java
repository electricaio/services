package io.electrica.metric.webhook.invocation.model;

public enum WebhookInvocationStatus {
    /**
     * Webhook without result invoked.
     */
    Invoked,
    /**
     * Webhook was invoked and pending for result.
     */
    Pending,
    /**
     * Webhook invoked and result received.
     */
    Success,
    /**
     * Webhook invoked and error received.
     */
    Error
}
