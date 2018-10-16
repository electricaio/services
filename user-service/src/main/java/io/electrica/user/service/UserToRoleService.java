package io.electrica.user.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.common.security.RoleType;
import io.electrica.user.model.Role;
import io.electrica.user.model.User;
import io.electrica.user.model.UserToRole;
import io.electrica.user.repository.UserToRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;

@Component
public class UserToRoleService extends AbstractService<UserToRole> {

    private final UserToRoleRepository userToRoleRepository;
    private final RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public UserToRoleService(UserToRoleRepository userRoleRepository, RoleService roleService) {
        this.userToRoleRepository = userRoleRepository;
        this.roleService = roleService;
    }

    public void addRoleToUser(User user, RoleType roleType) {
        Role role = new Role();
        role.setType(roleType);

        UserToRole userToRole = new UserToRole();
        userToRole.setRole(role);
        userToRole.setUser(user);
        create(userToRole);
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    protected UserToRole executeCreate(UserToRole newEntity) {
        Long userId = newEntity.getUser().getId();
        if (userId != null) {
            User touchedUser = entityManager.getReference(User.class, userId);
            newEntity.setUser(touchedUser);
        }

        RoleType roleType = newEntity.getRole().getType();
        if (roleType != null) {
            Role touchedRole = roleService.findByType(roleType);
            newEntity.setRole(touchedRole);
        }

        return userToRoleRepository.save(newEntity);
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

