package io.electrica.connector.hub.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import org.springframework.stereotype.Component;

@Component
public class ConnectionDtoService extends AbstractDtoService<Connection, ConnectDto, ConnectionDto> {

    private final ConnectionService connectionService;

    public ConnectionDtoService(ConnectionService connectionService) {
        this.connectionService = connectionService;
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
