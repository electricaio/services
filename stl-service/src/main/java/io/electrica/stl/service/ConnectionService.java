package io.electrica.stl.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.stl.model.Connection;
import io.electrica.stl.model.Connector;
import io.electrica.stl.repository.ConnectionRepository;
import io.electrica.stl.repository.ConnectorRepository;
import io.electrica.stl.rest.dto.ConnectDto;
import io.electrica.stl.rest.dto.ConnectionDto;
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
