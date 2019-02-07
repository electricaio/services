package io.electrica.metric.common.mq.connection.invocation.event;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.metric.common.mq.MetricEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionInvocationResultEvent implements MetricEvent {
    @NotNull
    private UUID invocationId;

    @NotNull
    private UUID instanceId;

    @NotNull
    private Long userId;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long accessKeyId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Long connectionId;

    @NotNull
    private Long connectionRevisionVersion;

    private Long authorizationId;

    @NotNull
    private Long connectorId;

    @NotNull
    private Long connectorRevisionVersion;

    @NotBlank
    private String ern;

    @NotNull
    private Boolean success;

    private JsonNode result;

    private String errorCode;

    private String errorMessage;

    private String stackTrace;

    private List<String> errorPayload;
}
