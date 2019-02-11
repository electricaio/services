package io.electrica.metric.connection.invocation.rest;

import io.electrica.metric.connection.invocation.dto.ConnectionInvocationDto;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.V1;

public interface ConnectionInvocationController {
    String PREFIX = V1 + "/metrics/connection-invocations";

    @GetMapping(path = PREFIX + "/connection-invocations")
    List<ConnectionInvocationDto> getConnectionInvocations(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "instanceId", required = false) UUID instanceId,
            @RequestParam(value = "connectionId", required = false) Long connectionId,
            @RequestParam(value = "connectorId", required = false) Long connectorId,
            @RequestParam(value = "status[]", required = false) Set<ConnectionInvocationStatus> status,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    );
}
