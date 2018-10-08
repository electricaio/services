package io.electrica.user.service;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.model.User;

import java.util.Optional;

/**
 * Service class for managing users.
 */
public interface UserService {

    Optional<User> findOneByLogin(String login);
    User createUser(CreateUserDto createUserDto);

}
