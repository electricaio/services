package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.user.model.Role;
import io.electrica.user.model.User;
import io.electrica.user.model.UserToRole;
import io.electrica.user.repository.UserRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;

@Component
public class UserRoleService extends AbstractService<UserToRole> {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;
    private final Provider<UserService> userService;

    @Inject
    public UserRoleService(UserRoleRepository userRoleRepository, RoleService roleService, Provider<UserService> userService) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    protected UserToRole executeCreate(UserToRole newEntity) {
        User user = newEntity.getUser();
        if (user != null) {
            Long userId = user.getId();
            if (userId != null) {
                User touchedUser = userService.get().findById(userId, true);
                newEntity.setUser(touchedUser);
            }
        }

        Role role = newEntity.getRole();
        if (role != null) {
            String name = role.getName();
            if (name != null) {
                Role touchedRole = roleService.findByName(name);
                newEntity.setRole(touchedRole);
            }
        }

        return userRoleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(UserToRole merged, UserToRole update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<UserToRole, Long> getRepository() {
        return userRoleRepository;
    }
}
