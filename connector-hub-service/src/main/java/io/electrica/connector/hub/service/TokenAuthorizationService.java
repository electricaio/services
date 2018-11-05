package io.electrica.connector.hub.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.TokenAuthorization;
import io.electrica.connector.hub.repository.TokenAuthorizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenAuthorizationService extends AbstractService<TokenAuthorization> {

    private final TokenAuthorizationRepository tokenAuthorizationRepository;
    private final ConnectionService connectionService;

    public TokenAuthorizationService(
            TokenAuthorizationRepository tokenAuthorizationRepository,
            ConnectionService connectionService
    ) {
        this.tokenAuthorizationRepository = tokenAuthorizationRepository;
        this.connectionService = connectionService;
    }

    @Transactional
    public TokenAuthorization createAndAssignToConnection(Long connectionId, TokenAuthorization authorization) {
        Connection connection = connectionService.findById(connectionId, true);
        if (connection.getAuthorization() != null) {
            throw new BadRequestServiceException("Authorization already exist, use update");
        }

        // ToDo can be optimized fetching connector
        AuthorizationType authorizationType = connection.getConnector().getAuthorizationType();
        if (authorizationType != AuthorizationType.Token) {
            throw new BadRequestServiceException("Connector has another authorization type: " + authorizationType);
        }

        TokenAuthorization result = create(authorization);
        connection.setAuthorization(result);
        connectionService.update(connectionId, connection);
        return result;
    }

    @Override
    protected TokenAuthorization executeCreate(TokenAuthorization newEntity) {
        return tokenAuthorizationRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(TokenAuthorization merged, TokenAuthorization update) {
        merged.setToken(update.getToken());
    }

    @Override
    protected JpaRepository<TokenAuthorization, Long> getRepository() {
        return tokenAuthorizationRepository;
    }
}
