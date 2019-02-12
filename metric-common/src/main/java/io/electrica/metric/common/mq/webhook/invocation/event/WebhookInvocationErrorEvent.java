package io.electrica.metric.common.mq.webhook.invocation.event;

import io.electrica.metric.common.mq.MetricEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookInvocationErrorEvent implements MetricEvent {
    @NotNull
    private UUID messageId;

    @NotNull
    private String errorMessage;

    @NotNull
    private LocalDateTime errorTime;
}
