package io.electrica.metric.common.mq.webhook.invocation.event;

import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.webhook.dto.WebhookScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookInvocationEvent implements MetricEvent {
    @NotNull
    private UUID messageId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private UUID webhookId;

    @NotNull
    private UUID webhookServiceId;

    @NotNull
    private String webhookName;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long userId;

    @NotNull
    private Long accessKeyId;

    @NotNull
    private Boolean isPublic;

    @NotNull
    private WebhookScope scope;

    private Long connectorId;

    private String connectorErn;

    private Long connectionId;

    private Map<String, String> properties;

    @NotNull
    private Boolean expectedResult;

    private String expectedContentType;

    @NotNull
    private String payload;

    @NotNull
    private String contentType;
}
