package io.electrica.connector.hub.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.service.ConnectorService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectorDtoService extends AbstractDtoService<Connector, CreateConnectorDto, ConnectorDto> {

    private final ConnectorService connectorService;

    public ConnectorDtoService(Mapper mapper, ConnectorService connectorService) {
        this.mapper = mapper;
        this.connectorService = connectorService;
    }

    public List<ConnectorDto> findAll() {
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
    protected Class<ConnectorDto> getDtoClass() {
        return ConnectorDto.class;
    }
}
