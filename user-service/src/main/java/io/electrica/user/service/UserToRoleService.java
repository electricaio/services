package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Role;
import io.electrica.user.model.User;
import io.electrica.user.model.UserToRole;
import io.electrica.user.repository.UserToRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;

@Component
public class UserToRoleService extends AbstractService<UserToRole> {

    private final UserToRoleRepository userToRoleRepository;
    private final RoleService roleService;
    private final Provider<UserService> userService;

    @Inject
    public UserToRoleService(UserToRoleRepository userRoleRepository, RoleService roleService,
                             Provider<UserService> userService) {
        this.userToRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.userService = userService;
    }


    public void addDefaultRoleToUser(User user) {
        Role defaultRole = roleService.findDefaultRole();
        UserToRole userToRole = new UserToRole();
        userToRole.setRole(defaultRole);
        userToRole.setUser(user);
        userToRoleRepository.save(userToRole);
    }


    @Override
    protected UserToRole executeCreate(UserToRole newEntity) {
        // TODO : Take it as diff task
        return null;
    }

    @Override
    protected void executeUpdate(UserToRole merged, UserToRole update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Collection<String> getContainerValidators() {
        return Collections.emptyList();
    }

    @Override
    protected Collection<EntityValidator<UserToRole>> getValidators() {
        return Collections.emptyList();
    }

    @Override
    protected JpaRepository<UserToRole, Long> getRepository() {
        return userToRoleRepository;
    }
}

