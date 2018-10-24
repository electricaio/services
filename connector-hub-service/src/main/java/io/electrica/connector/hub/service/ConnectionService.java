package io.electrica.connector.hub.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.repository.ConnectionRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ConnectionService extends AbstractService<Connection> {

    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Override
    protected Connection executeCreate(Connection newEntity) {
        final Connector connector = getReference(Connector.class, newEntity.getConnector().getId());
        newEntity.setConnector(connector);
        return connectionRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Connection merged, Connection update) {
        throw new NotImplementedException("");
    }

    @Override
    protected JpaRepository<Connection, Long> getRepository() {
        return connectionRepository;
    }
}
