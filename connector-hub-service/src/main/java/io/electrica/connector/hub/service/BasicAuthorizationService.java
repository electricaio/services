package io.electrica.connector.hub.service;

import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.BasicAuthorization;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.BasicAuthorizationRepository;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BasicAuthorizationService {

    private final AuthorizationService authorizationService;
    private final BasicAuthorizationRepository basicAuthorizationRepository;

    public BasicAuthorizationService(AuthorizationService authorizationRepository,
                                     BasicAuthorizationRepository basicAuthorizationRepository) {
        this.authorizationService = authorizationRepository;
        this.basicAuthorizationRepository = basicAuthorizationRepository;
    }

    /**
     * Given the connection id,
     * it fetches or creates authorization if not exists.
     * <p>
     * For that authorization id,
     * it fetches the corresponding basic authorization if exists
     * and sets the new credentials if they have changed.
     * <p>
     * If basic authorization does not exists, it creates a new one.
     */
    @Transactional
    public Authorization upsert(Long connectionId, CreateBasicAuthorizationDto dto) {

        final Authorization authorization = authorizationService
                .createIfAbsent(connectionId, AuthorizationTypeName.BASIC_AUTHORIZATION, dto);

        final BasicAuthorization basicAuthorization = basicAuthorizationRepository
                .findOneByAuthorizationId(authorization.getId())
                .orElse(new BasicAuthorization());

        basicAuthorization.setAuthorization(authorization);
        basicAuthorization.setUserHash(dto.getUser());
        basicAuthorization.setPasswordHash(dto.getPassword());
        basicAuthorizationRepository.save(basicAuthorization);

        return authorization;
    }
}
