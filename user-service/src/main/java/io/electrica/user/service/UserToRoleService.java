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
import java.util.ArrayList;
import java.util.List;

@Component
public class UserToRoleService extends AbstractService<UserToRole> {

    private final UserToRoleRepository userToRoleRepository;
    private final RoleService roleService;

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
        User user = getReference(User.class, userId);
        newEntity.setUser(user);

        RoleType roleType = newEntity.getRole().getType();
        Role role = roleService.findByType(roleType);
        newEntity.setRole(role);

        return userToRoleRepository.save(newEntity);
    }

    @Override
    protected EntityValidator<UserToRole> getValidator() {
        return create -> {
            List<String> required = new ArrayList<>();

            User user = create.getUser();
            if (user == null) {
                required.add("user");
            } else if (user.getId() == null) {
                required.add("user.id");
            }

            Role role = create.getRole();
            if (role == null) {
                required.add("role");
            } else if (role.getType() == null) {
                required.add("role.type");
            }

            if (!required.isEmpty()) {
                throw new IllegalArgumentException("Required fields: " + String.join(", ", required));
            }
        };
    }

    @Override
    protected void executeUpdate(UserToRole merged, UserToRole update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String[] getContainerValidators() {
        return new String[]{};
    }

    @Override
    protected JpaRepository<UserToRole, Long> getRepository() {
        return userToRoleRepository;
    }
}

