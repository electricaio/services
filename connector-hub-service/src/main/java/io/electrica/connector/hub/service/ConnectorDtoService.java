package io.electrica.connector.hub.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.rest.dto.CreateConnectorDto;
import io.electrica.connector.hub.rest.dto.ReadConnectorDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectorDtoService extends AbstractDtoService<Connector, CreateConnectorDto, ReadConnectorDto> {

    private final ConnectorService connectorService;

    public ConnectorDtoService(Mapper mapper, ConnectorService connectorService) {
        this.mapper = mapper;
        this.connectorService = connectorService;
    }

    public List<ReadConnectorDto> findAll() {
        return toDto(connectorService.findAll());
    }

    @Override
    protected AbstractService<Connector> getService() {
        return connectorService;
    }

    @Override
    protected Class<Connector> getEntityClass() {
        return Connector.class;
    }

    @Override
    protected Class<ReadConnectorDto> getDtoClass() {
        return ReadConnectorDto.class;
    }
}