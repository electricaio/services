package io.electrica.connector.hub.service;

import io.electrica.connector.hub.model.*;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AuthorizationRepository;
import io.electrica.connector.hub.repository.AuthorizationTypeRepository;
import io.electrica.connector.hub.repository.BasicAuthorizationRepository;
import io.electrica.connector.hub.repository.TokenAuthorizationRepository;
import io.electrica.connector.hub.rest.dto.AuthorizationDto;
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

    public Authorization createBasicAuth(Long connectionId, CreateBasicAuthorizationDto dto) {

        final Authorization authorization = authorizationRepository
                .findOneByNameAndConnectionId(dto.getName(), connectionId)
                .orElseGet(() -> createAuthorization(
                        connectionId,
                        AuthorizationTypeName.BASIC_AUTHORIZATION,
                        dto
                ));

        basicAuthorizationRepository.findOneByAuthorizationId(authorization.getId())
                .orElseGet(() -> {
                    final BasicAuthorization model = new BasicAuthorization();
                    model.setUserHash(dto.getUser());
                    model.setPasswordHash(dto.getPassword());
                    model.setAuthorization(authorization);

                    return basicAuthorizationRepository.save(model);
                });

        return authorization;
    }

    public Authorization createTokenAuth(Long connectionId, CreateTokenAuthorizationDto dto) {

        final Authorization authorization = authorizationRepository
                .findOneByNameAndConnectionId(dto.getName(), connectionId)
                .orElseGet(() -> createAuthorization(
                        connectionId,
                        AuthorizationTypeName.TOKEN_AUTHORIZATION,
                        dto));

        tokenAuthorizationRepository.findOneByAuthorizationId(authorization.getId())
                .orElseGet(() -> {
                    final TokenAuthorization model = new TokenAuthorization();
                    model.setToken(dto.getToken());
                    model.setAuthorization(authorization);
                    return tokenAuthorizationRepository.save(model);
                });

        return authorization;
    }

    private Authorization createAuthorization(Long connectionId, AuthorizationTypeName typeName, AuthorizationDto dto) {
        final Authorization model = new Authorization();
        model.setName(dto.getName());
        dto.getTenantRefIdOpt().ifPresent(model::setTenantRefId);

        final AuthorizationType type = authorizationTypeRepository.findOneByName(typeName)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity not found by name %s", typeName)));
        model.setType(type);

        final Connection connection = em.getReference(Connection.class, connectionId);
        model.setConnection(connection);

        return authorizationRepository.save(model);
    }
}
