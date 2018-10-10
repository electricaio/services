package io.electrica.user.service.impl;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import io.electrica.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Service implementation class for managing users.
 */
public class UserServiceImpl extends AbstractService<User> {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Override
    protected Collection<String> getContainerValidators() {
        return null;
    }

    @Override
    protected Collection<EntityValidator<User>> getValidators() {
        return null;
    }
}
