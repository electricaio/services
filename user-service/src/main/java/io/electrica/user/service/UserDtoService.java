package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.common.security.RoleType;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User Dto Service implementation class for managing users.
 */
@Component
public class UserDtoService extends AbstractDtoService<User, UserDto> {

    private final UserService userService;
    private final UserToRoleService userToRoleService;

    @Inject
    public UserDtoService(UserService userService, UserToRoleService userToRoleService) {
        this.userService = userService;
        this.userToRoleService = userToRoleService;
    }

    @Override
    public UserDto create(UserDto dto) {
        User user = userService.create(toEntity(dto));
        // ToDo create all users with OrgUser role
        userToRoleService.addRoleToUser(user, RoleType.OrgUser);
        return toDto(user);
    }

    @Override
    protected AbstractService<User> getService() {
        return userService;
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected Class<UserDto> getDtoClass() {
        return UserDto.class;
    }

}
