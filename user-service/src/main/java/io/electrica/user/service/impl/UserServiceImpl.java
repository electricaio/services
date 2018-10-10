package io.electrica.user.service.impl;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import io.electrica.user.service.RoleService;
import io.electrica.user.service.UserRoleService;
import io.electrica.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.Optional;

/**
 * Service implementation class for managing users.
 */
public class UserServiceImpl extends AbstractService<User> {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    public UserServiceImpl(UserRepository userRepository, UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Override
    protected User executeCreate(User newEntity) {
        User user = userRepository.save(newEntity);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    @Override
    protected void executeUpdate(User merged, User update) {
        // TODO
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
}
