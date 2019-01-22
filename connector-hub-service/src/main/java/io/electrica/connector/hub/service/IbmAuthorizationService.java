package io.electrica.connector.hub.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.IbmAuthorization;
import io.electrica.connector.hub.repository.IbmAuthorizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IbmAuthorizationService extends AbstractService<IbmAuthorization> {

    private final IbmAuthorizationRepository ibmAuthorizationRepository;
    private final ConnectionService connectionService;

    public IbmAuthorizationService(
            IbmAuthorizationRepository ibmAuthorizationRepository,
            ConnectionService connectionService
    ) {
        this.ibmAuthorizationRepository = ibmAuthorizationRepository;
        this.connectionService = connectionService;
    }

    @Transactional
    public IbmAuthorization createAndAssignToConnection(Long connectionId, IbmAuthorization authorization) {
        Connection connection = connectionService.findById(connectionId, true);
        if (connection.getAuthorization() != null) {
            throw new BadRequestServiceException("Authorization already exist, use update");
        }

        // ToDo can be optimized fetching connector
        AuthorizationType authorizationType = connection.getConnector().getAuthorizationType();
        if (authorizationType != AuthorizationType.Ibm) {
            throw new BadRequestServiceException("Connector has another authorization type: " + authorizationType);
        }

        IbmAuthorization result = create(authorization);
        connection.setAuthorization(result);
        connectionService.update(connectionId, connection);
        return result;
    }

    @Override
    protected IbmAuthorization executeCreate(IbmAuthorization newEntity) {
        return ibmAuthorizationRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(IbmAuthorization merged, IbmAuthorization update) {
        merged.setIntegrationId(update.getIntegrationId());
        merged.setClientId(update.getClientId());
    }

    @Override
    protected JpaRepository<IbmAuthorization, Long> getRepository() {
        return ibmAuthorizationRepository;
    }
}
