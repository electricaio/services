package io.electrica.connector.hub.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.repository.ConnectionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public Connection findByIdWithConnectorAndAuthorization(Long connectionId) {
        return connectionRepository.findByIdWithConnectorAndAuthorization(connectionId)
                .orElseThrow(() -> new BadRequestServiceException("Connection not found: " + connectionId));
    }

    public List<Connection> findAllByUser(Long userId) {
        return connectionRepository.findAllByUser(userId);
    }

    @Override
    protected void executeUpdate(Connection merged, Connection update) {
        merged.setName(update.getName());

        Authorization authorization = update.getAuthorization();
        if (authorization != null) {
            Authorization authorizationRef = getReference(Authorization.class, authorization.getId());
            merged.setAuthorization(authorizationRef);
        }
    }

    public List<Connection> findAllByAccessKey(Long accessKeyId,
                                               @Nullable String connectionName, @Nullable String ern) {
        return connectionRepository.findByAccessKeyWithFilter(accessKeyId, connectionName, ern);
    }

    @Override
    protected JpaRepository<Connection, Long> getRepository() {
        return connectionRepository;
    }
}
