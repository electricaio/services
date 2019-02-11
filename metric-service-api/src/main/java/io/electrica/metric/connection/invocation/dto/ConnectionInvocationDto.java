package io.electrica.metric.connection.invocation.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionInvocationDto {
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
    private ConnectionInvocationStatus status;

    private String action;

    private JsonNode parameters;

    private JsonNode payload;

    private JsonNode result;

    private String errorCode;

    private String errorMessage;

    private String stackTrace;

    private List<String> errorPayload;
}
