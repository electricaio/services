package io.electrica.user.service;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;

import java.util.Optional;

/**
 * Service class for managing users.
 */
public interface UserService {

    Optional<User> findOneByLogin(String login);
    UserDto createUser(CreateUserDto createUserDto);
    UserDto updateUser(UserDto userDto);

}
