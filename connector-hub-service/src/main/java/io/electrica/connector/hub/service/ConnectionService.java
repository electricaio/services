package io.electrica.connector.hub.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.repository.ConnectionRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ConnectionService extends AbstractService<Connection> {

    private final ConnectorService connectorService;

    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectorService connectorService,
                             ConnectionRepository connectionRepository) {
        this.connectorService = connectorService;
        this.connectionRepository = connectionRepository;
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    protected Connection executeCreate(Connection newEntity) {

        final Connector connector = connectorService.findById(newEntity.getConnector().getId(), true);

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
