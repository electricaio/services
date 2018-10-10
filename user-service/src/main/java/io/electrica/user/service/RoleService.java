package io.electrica.user.service;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Role;
import io.electrica.user.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

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

    public Role findByName(String name) {
        return roleRepository.findOneByName(name)
                .orElseThrow(() -> new EntityNotFoundServiceException("role: " + name));
    }

    @Override
    protected Role executeCreate(Role newEntity) {
        return roleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Role merged, Role update) {
        merged.setName(update.getName());
    }

    @Override
    protected JpaRepository<Role, Long> getRepository() {
        return roleRepository;
    }

    @Override
    protected Collection<String> getContainerValidators() {
        return null;
    }

    @Override
    protected Collection<EntityValidator<Role>> getValidators() {
        return null;
    }
}
