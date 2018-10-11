package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.Organization;
import io.electrica.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User Dto Service implementation class for managing users.
 */
@Component
public class UserDtoService extends AbstractDtoService<User, UserDto> {

    private static final Logger log = LoggerFactory.getLogger(UserDtoService.class);

    private final UserService userService;

    @Inject
    public UserDtoService(UserService userService) {
        this.userService = userService;
    }

    public UserDto createUser(CreateUserDto user) {
        User newUser = userService.create(toEntity(user));
        UserDto userDto = toDto(newUser);
        return userDto;
    }

    // TODO Once we founf workaround of Dozer library issue . We will remove it.
    private User toEntity(CreateUserDto userDto) {
        User user = new User();
        user.setUuid(userDto.getUuid());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setOrganization(toEntity(userDto.getOrganizationId()));
        user.setSaltedPassword(userDto.getPassword());
        return user;
    }

    private Organization toEntity(Long organizationId) {
        Organization org = new Organization();
        org.setId(organizationId);
        return org;
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
