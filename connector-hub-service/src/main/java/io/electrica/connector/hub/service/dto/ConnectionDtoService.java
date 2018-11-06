package io.electrica.connector.hub.service.dto;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.service.ConnectionService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectionDtoService extends AbstractDtoService<Connection, CreateConnectionDto, ConnectionDto> {

    private final IdentityContextHolder identityContextHolder;

    private final ConnectionService connectionService;

    public ConnectionDtoService(IdentityContextHolder identityContextHolder, ConnectionService connectionService) {
        this.identityContextHolder = identityContextHolder;
        this.connectionService = connectionService;
    }

    @Override
    public ConnectionDto create(CreateConnectionDto persistentDto) {
        final Connection connection = toCreateEntity(persistentDto);

        final Identity identity = identityContextHolder.getIdentity();
        connection.setUserId(identity.getUserId());
        connection.setOrganizationId(identity.getOrganizationId());

        return toDto(
                getService().create(connection)
        );
    }

    public List<ConnectionDto> findAllByAccessKey(@Nullable String connectionName, @Nullable String ern) {
        return toDto(connectionService.findAllByAccessKey(identityContextHolder.getIdentity().getAccessKeyId(),
                connectionName, ern));
    }


    public List<ConnectionDto> findAllByUser(Long userId) {
        return toDto(connectionService.findAllByUser(userId));
    }

    @Override
    protected AbstractService<Connection> getService() {
        return connectionService;
    }

    @Override
    protected Class<Connection> getEntityClass() {
        return Connection.class;
    }

    @Override
    protected Class<ConnectionDto> getDtoClass() {
        return ConnectionDto.class;
    }
}
