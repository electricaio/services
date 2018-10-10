package io.electrica.user.service.impl;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import io.electrica.user.service.RoleService;
import io.electrica.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;

/**
 * Service implementation class for managing users.
 */
public class UserServiceImpl extends AbstractService<User> implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public Optional<User> findOneByLogin(String login) {
        return userRepository.findOneByEmailIgnoreCase(login);
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = toEntity(createUserDto);
        user = userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return toDto(user);

    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        return null;
    }

    public UserDto toDto(User e) {
        return new UserDto(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                Collections.EMPTY_SET,
                Collections.EMPTY_SET
        );
    }

    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        if (userDto instanceof CreateUserDto) {
            user.setSaltedPassword(((CreateUserDto) userDto).getPassword());
        }
        return user;
    }
}
