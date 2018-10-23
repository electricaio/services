package io.electrica.connector.hub.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.repository.ConnectionRepository;
import io.electrica.connector.hub.repository.ConnectorRepository;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Component
public class ConnectionService {

    private final ConnectorRepository connectorRepository;

    private final ConnectionRepository connectionRepository;

    private final Mapper mapper;

    public ConnectionService(ConnectorRepository connectorRepository,
                             ConnectionRepository connectionRepository,
                             Mapper mapper) {
        this.connectorRepository = connectorRepository;
        this.connectionRepository = connectionRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ConnectionDto create(ConnectDto dto) {

        final Connector connector = connectorRepository.findById(dto.getConnectorId())
                .orElseThrow(EntityNotFoundException::new);

        final Connection model = new Connection(connector, dto.getAccessKeyId());
        connectionRepository.save(model);

        return mapper.map(model, ConnectionDto.class);
    }
}
