package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

/**
 * User Service implementation class for managing users.
 */
@Component
public class UserService extends AbstractService<User> {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected Collection<String> getContainerValidators() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected Collection<EntityValidator<User>> getValidators() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected User executeCreate(User newEntity) {
        User user = userRepository.save(newEntity);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    @Override
    protected void executeUpdate(User merged, User update) {

    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
}
