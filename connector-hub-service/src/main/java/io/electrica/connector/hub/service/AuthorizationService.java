package io.electrica.connector.hub.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.BasicAuthorization;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AuthorizationRepository;
import io.electrica.connector.hub.repository.AuthorizationTypeRepository;
import io.electrica.connector.hub.repository.BasicAuthorizationRepository;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Component
public class AuthorizationService {

    private final ConnectionService connectionService;

    private final AuthorizationRepository authorizationRepository;

    private final AuthorizationTypeRepository authorizationTypeRepository;

    private final BasicAuthorizationRepository basicAuthorizationRepository;

    private final Mapper mapper;

    private final EntityManager em;

    public AuthorizationService(ConnectionService connectionService,
                                AuthorizationRepository authorizationRepository,
                                AuthorizationTypeRepository authorizationTypeRepository,
                                BasicAuthorizationRepository basicAuthorizationRepository,
                                Mapper mapper,
                                EntityManager em) {
        this.connectionService = connectionService;
        this.authorizationRepository = authorizationRepository;
        this.authorizationTypeRepository = authorizationTypeRepository;
        this.basicAuthorizationRepository = basicAuthorizationRepository;
        this.em = em;
        this.mapper = mapper;
    }

    public ReadAuthorizationDto createBasicAuth(Long connectionId, CreateBasicAuthorizationDto authorizationDto) {
        final Connection connection = connectionService.findById(connectionId, true);

        final Authorization authorization = authorizationRepository.findOneByConnectionId(connection.getId())
                .orElseGet(() -> createAuthorization(AuthorizationTypeName.BASIC_AUTHORIZATION, connectionId));

        basicAuthorizationRepository.findOneByAuthorizationId(authorization.getId())
                .orElseGet(() -> {
                    final BasicAuthorization model = new BasicAuthorization();
                    model.setUserHash(authorizationDto.getUser());
                    model.setPasswordHash(authorizationDto.getPassword());
                    model.setAuthorization(authorization);

                    return basicAuthorizationRepository.save(model);
                });

        return mapper.map(authorization, ReadAuthorizationDto.class);
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
