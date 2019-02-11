package io.electrica.metric.webhook.invocation.dto;

import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import io.electrica.webhook.dto.WebhookScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookInvocationDto {
    @NotNull
    private UUID messageId;

    @NotNull
    private UUID webhookId;

    @NotNull
    private UUID webhookServiceId;

    private String webhookName;

    private Long organizationId;

    private Long userId;

    private Long accessKeyId;

    private Boolean isPublic;

    private WebhookScope scope;

    private Long connectorId;

    private String connectorErn;

    private Long connectionId;

    private Map<String, String> properties;

    private Boolean expectedResult;

    private String expectedContentType;

    private String payload;

    private String contentType;

    private UUID sdkInstanceId;

    private String resultPayload;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull
    private WebhookInvocationStatus status;

    private String errorMessage;

    private String stackTrace;
}
