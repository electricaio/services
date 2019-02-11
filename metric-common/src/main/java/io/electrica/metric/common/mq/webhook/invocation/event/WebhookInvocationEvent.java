package io.electrica.metric.common.mq.webhook.invocation.event;

import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.webhook.message.WebhookMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookInvocationEvent implements MetricEvent {
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private WebhookMessage webhookMessage;
}
