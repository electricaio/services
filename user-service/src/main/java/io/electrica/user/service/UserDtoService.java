package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
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
