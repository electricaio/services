package io.electrica.user.service.impl;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    @Override
    public Optional<User> findOneByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public User createUser(CreateUserDto createUserDto) {
        return null;
    }
}
