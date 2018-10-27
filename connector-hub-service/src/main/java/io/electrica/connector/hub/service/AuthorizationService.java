package io.electrica.connector.hub.service;

import io.electrica.connector.hub.model.*;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AuthorizationRepository;
import io.electrica.connector.hub.repository.AuthorizationTypeRepository;
import io.electrica.connector.hub.repository.BasicAuthorizationRepository;
import io.electrica.connector.hub.repository.TokenAuthorizationRepository;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Component
public class AuthorizationService {

    private final AuthorizationRepository authorizationRepository;

    private final AuthorizationTypeRepository authorizationTypeRepository;

    private final BasicAuthorizationRepository basicAuthorizationRepository;

    private final TokenAuthorizationRepository tokenAuthorizationRepository;

    private final EntityManager em;

    public AuthorizationService(AuthorizationRepository authorizationRepository, AuthorizationTypeRepository
            authorizationTypeRepository, BasicAuthorizationRepository basicAuthorizationRepository,
            TokenAuthorizationRepository tokenAuthorizationRepository, EntityManager em) {
        this.authorizationRepository = authorizationRepository;
        this.authorizationTypeRepository = authorizationTypeRepository;
        this.basicAuthorizationRepository = basicAuthorizationRepository;
        this.tokenAuthorizationRepository = tokenAuthorizationRepository;
        this.em = em;
    }

    public Authorization createBasicAuth(Long connectionId, CreateBasicAuthorizationDto authorizationDto) {

        final Authorization authorization = authorizationRepository.findOneByConnectionId(connectionId)
                .orElseGet(() -> createAuthorization(AuthorizationTypeName.BASIC_AUTHORIZATION, connectionId));

        basicAuthorizationRepository.findOneByAuthorizationId(authorization.getId())
                .orElseGet(() -> {
                    final BasicAuthorization model = new BasicAuthorization();
                    model.setUserHash(authorizationDto.getUser());
                    model.setPasswordHash(authorizationDto.getPassword());
                    model.setAuthorization(authorization);

                    return basicAuthorizationRepository.save(model);
                });

        return authorization;
    }

    public Authorization createTokenAuth(Long connectionId, CreateTokenAuthorizationDto authorizationDto) {

        final Authorization authorization = authorizationRepository.findOneByConnectionId(connectionId)
                .orElseGet(() -> createAuthorization(AuthorizationTypeName.TOKEN_AUTHORIZATION, connectionId));

        tokenAuthorizationRepository.findOneByAuthorizationId(authorization.getId())
                .orElseGet(() -> {
                    final TokenAuthorization model = new TokenAuthorization();
                    model.setToken(authorizationDto.getToken());
                    model.setAuthorization(authorization);
                    return tokenAuthorizationRepository.save(model);
                });

        return authorization;
    }

    private Authorization createAuthorization(AuthorizationTypeName typeName, Long connectionId) {
        final Authorization model = new Authorization();

        final AuthorizationType type = authorizationTypeRepository.findOneByName(typeName)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity not found by name %s", typeName)));
        model.setType(type);

        final Connection connection = em.getReference(Connection.class, connectionId);
        model.setConnection(connection);

        return authorizationRepository.save(model);
    }
}
