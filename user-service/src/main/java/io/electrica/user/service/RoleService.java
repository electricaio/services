package io.electrica.user.service;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.security.RoleType;
import io.electrica.user.model.Role;
import io.electrica.user.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Role Service implementation class for managing roles.
 */
@Component
public class RoleService extends AbstractService<Role> {

    private final RoleRepository roleRepository;

    @Inject
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByType(RoleType type) {
        return roleRepository.findOneByType(type)
                .orElseThrow(() -> new EntityNotFoundServiceException("Role with type: " + type));
    }

    @Override
    protected Role executeCreate(Role newEntity) {
        return roleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Role merged, Role update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<Role, Long> getRepository() {
        return roleRepository;
    }

}
