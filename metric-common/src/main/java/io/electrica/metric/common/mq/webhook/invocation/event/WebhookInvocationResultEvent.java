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
public class WebhookInvocationResultEvent implements MetricEvent {
    @NotNull
    private UUID messageId;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private UUID webhookId;

    @NotNull
    private UUID sdkInstanceId;

    @NotNull
    private UUID webhookServiceId;

    private String resultPayload;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long userId;

    @NotNull
    private Long accessKeyId;
}
