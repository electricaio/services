package io.electrica.user.service.dto;

import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.common.security.RoleType;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.UserService;
import io.electrica.user.service.UserToRoleService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * User Dto Service implementation class for managing users.
 */
@Component
public class UserDtoService extends AbstractDtoService<User, CreateUserDto, UserDto> {

    private final UserService userService;
    private final UserToRoleService userToRoleService;
    private final IdentityContextHolder identityContextHolder;

    @Inject
    public UserDtoService(UserService userService, UserToRoleService userToRoleService,
                          IdentityContextHolder identityContextHolder) {
        this.userService = userService;
        this.userToRoleService = userToRoleService;
        this.identityContextHolder = identityContextHolder;
    }

    @Override
    public UserDto create(CreateUserDto dto) {
        User user = userService.create(toCreateEntity(dto));
        // ToDo create all users with OrgUser role
        userToRoleService.addRoleToUser(user, RoleType.OrgUser);
        return toDto(user);
    }

    public List<UserDto> getUsersForOrg(Long orgId) {
        return toDto(userService.getUsersForOrg(orgId));
    }

    public UserDto findByMe() {
        long userId = identityContextHolder.getIdentity().getUserId();
        return findById(userId, true);
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
