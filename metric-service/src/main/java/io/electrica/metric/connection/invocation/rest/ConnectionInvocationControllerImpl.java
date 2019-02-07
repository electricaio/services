package io.electrica.metric.connection.invocation.rest;

import io.electrica.metric.connection.invocation.dto.ConnectionInvocationDto;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.metric.connection.invocation.service.dto.ConnectionInvocationDtoService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class ConnectionInvocationControllerImpl implements ConnectionInvocationController {
    private final ConnectionInvocationDtoService service;

    @Inject
    public ConnectionInvocationControllerImpl(ConnectionInvocationDtoService service) {
        this.service = service;
    }

    @PreAuthorize("" +
            "#common.hasPermission('ReadConnectionInvocation') AND ( " +
            " #common.isUser(#userId) OR " +
            " #metric.accessKeyBelongsUser(#accessKeyId) OR " +
            " #metric.instanceBelongsCurrentUser(#instanceId) OR " +
            " #metric.connectionBelongsCurrentUser(#connectionId) OR" +
            " #common.isSuperAdmin()" +
            ")")
    @Override
    public List<ConnectionInvocationDto> getConnectionInvocations(
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
    ) {
        return service.getConnectionInvocations(pageable, userId, organizationId, accessKeyId, instanceId,
                connectionId, connectorId, status, startTime, endTime);
    }
}
