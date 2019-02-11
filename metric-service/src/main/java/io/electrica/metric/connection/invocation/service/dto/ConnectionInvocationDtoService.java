package io.electrica.metric.connection.invocation.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.metric.connection.invocation.dto.ConnectionInvocationDto;
import io.electrica.metric.connection.invocation.model.ConnectionInvocation;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.metric.connection.invocation.service.ConnectionInvocationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ConnectionInvocationDtoService {
    private final ConnectionInvocationService connectionInvocationService;
    private final Mapper mapper;

    @Inject
    public ConnectionInvocationDtoService(ConnectionInvocationService connectionInvocationService, Mapper mapper) {
        this.connectionInvocationService = connectionInvocationService;
        this.mapper = mapper;
    }

    public ConnectionInvocationDto upsert(ConnectionInvocationDto connectionInvocationDto) {
        return toDto(connectionInvocationService.upsert(toEntity(connectionInvocationDto)));
    }

    public List<ConnectionInvocationDto> getConnectionInvocations(
            Pageable pageable,
            Long userId,
            Long organizationId,
            Long accessKeyId,
            UUID instanceId,
            Long connectionId,
            Long connectorId,
            Set<ConnectionInvocationStatus> status,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return connectionInvocationService.getConnectionInvocations(
                pageable,
                userId,
                organizationId,
                accessKeyId,
                instanceId,
                connectionId,
                connectorId,
                status,
                startTime,
                endTime
        ).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ConnectionInvocation toEntity(ConnectionInvocationDto dto) {
        return mapper.map(dto, ConnectionInvocation.class);
    }

    private ConnectionInvocationDto toDto(ConnectionInvocation entity) {
        return mapper.map(entity, ConnectionInvocationDto.class);
    }
}
