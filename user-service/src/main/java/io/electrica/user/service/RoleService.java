package io.electrica.user.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Role;
import io.electrica.user.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Role Service implementation class for managing roles.
 */
@Component
public class RoleService extends AbstractService<Role> {

    private final RoleRepository roleRepository;
    private static final String Default_Role = "Org-User";

    @Inject
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        return roleRepository.findOneByName(name)
                .orElseThrow(() -> new BadRequestServiceException("role: " + name));
    }

    public Role findDefaultRole() {
        return roleRepository.findOneByName(Default_Role)
                .orElseThrow(() -> new BadRequestServiceException("role: " + Default_Role + " qnot found"));
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
        return Arrays.asList(
                ContainerEntityValidator.TRIMMED_STRINGS,
                ContainerEntityValidator.AVOID_EMTPY_STRINGS
        );
    }

    @Override
    protected Collection<EntityValidator<Role>> getValidators() {
        return Collections.emptyList();
    }

    public String getDefaultRole(){
        return Default_Role;
    }
}
