package io.electrica.connector.hub.service;

import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AuthorizationRepository;
import io.electrica.connector.hub.repository.AuthorizationTypeRepository;
import io.electrica.connector.hub.rest.dto.AuthorizationDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Component
public class AuthorizationService {

    private final AuthorizationRepository authorizationRepository;
    private final AuthorizationTypeRepository authorizationTypeRepository;
    private final EntityManager em;

    public AuthorizationService(AuthorizationRepository authorizationRepository,
                                AuthorizationTypeRepository authorizationTypeRepository,
                                EntityManager em) {
        this.authorizationRepository = authorizationRepository;
        this.authorizationTypeRepository = authorizationTypeRepository;
        this.em = em;
    }

    @Transactional(readOnly = true)
    public List<Authorization> findAllByConnectionId(Long connectionId) {
        return authorizationRepository.findAllByConnectionId(connectionId);
    }

    /**
     * Given the connection id and type name, it searches for an authorization entity
     * and returns if exists,
     * otherwise it creates a new one with provided name and tenant ref id.
     * */
    @Transactional
    public Authorization createIfAbsent(Long connectionId, AuthorizationTypeName typeName, AuthorizationDto dto) {
        return authorizationRepository
                .findOneByNameAndConnectionId(dto.getName(), connectionId)
                .orElseGet(() -> {
                    final Authorization model = new Authorization();
                    model.setName(dto.getName());
                    dto.getTenantRefIdOpt().ifPresent(model::setTenantRefId);

                    final AuthorizationType type = authorizationTypeRepository.findOneByName(typeName)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    String.format("Entity not found by name %s", typeName))
                            );
                    model.setType(type);

                    final Connection connection = em.getReference(Connection.class, connectionId);
                    model.setConnection(connection);

                    return authorizationRepository.save(model);
                });
    }
}
