package io.electrica.stl.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.stl.model.Connector;
import io.electrica.stl.rest.dto.CreateConnectorDto;
import io.electrica.stl.rest.dto.ReadConnectorDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConnectorDtoService extends AbstractDtoService<Connector, CreateConnectorDto, ReadConnectorDto> {

    private final ConnectorService connectorService;

    public ConnectorDtoService(Mapper mapper, ConnectorService connectorService) {
        this.mapper = mapper;
        this.connectorService = connectorService;
    }

    public List<ReadConnectorDto> findAll() {
        return connectorService.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
