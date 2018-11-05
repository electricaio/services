package io.electrica.connector.hub.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.model.BasicAuthorization;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.repository.BasicAuthorizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BasicAuthorizationService extends AbstractService<BasicAuthorization> {

    private final BasicAuthorizationRepository basicAuthorizationRepository;
    private final ConnectionService connectionService;

    public BasicAuthorizationService(
            BasicAuthorizationRepository basicAuthorizationRepository,
            ConnectionService connectionService
    ) {
        this.basicAuthorizationRepository = basicAuthorizationRepository;
        this.connectionService = connectionService;
    }

    @Transactional
    public BasicAuthorization createAndAssignToConnection(Long connectionId, BasicAuthorization authorization) {
        Connection connection = connectionService.findById(connectionId, true);
        if (connection.getAuthorization() != null) {
            throw new BadRequestServiceException("Authorization already exist, use update");
        }

        // ToDo can be optimized fetching connector
        AuthorizationType authorizationType = connection.getConnector().getAuthorizationType();
        if (authorizationType != AuthorizationType.Basic) {
            throw new BadRequestServiceException("Connector has another authorization type: " + authorizationType);
        }

        BasicAuthorization result = create(authorization);
        connection.setAuthorization(result);
        connectionService.update(connectionId, connection);
        return result;
    }

    @Override
    protected BasicAuthorization executeCreate(BasicAuthorization newEntity) {
        return basicAuthorizationRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(BasicAuthorization merged, BasicAuthorization update) {
        merged.setUsername(update.getUsername());
        merged.setPassword(update.getPassword());
    }

    @Override
    protected JpaRepository<BasicAuthorization, Long> getRepository() {
        return basicAuthorizationRepository;
    }
}
