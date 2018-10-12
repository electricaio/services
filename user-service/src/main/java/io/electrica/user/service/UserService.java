package io.electrica.user.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Organization;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * User Service implementation class for managing users.
 */
@Component
public class UserService extends AbstractService<User> {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationService organizationService;

    @Inject
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.organizationService = organizationService;
    }


    @Override
    protected Collection<String> getContainerValidators() {
        return Arrays.asList(
                ContainerEntityValidator.TRIMMED_STRINGS,
                ContainerEntityValidator.AVOID_EMTPY_STRINGS
        );
    }

    @Override
    protected Collection<EntityValidator<User>> getValidators() {
        return Collections.emptyList();
    }

    @Override
    protected User executeCreate(User newEntity) {
        if (newEntity.getOrganization() == null || newEntity.getOrganization().getId() == null) {
            throw new BadRequestServiceException("Organization Id cannot be null");
        }
        Organization organization = organizationService.findById(newEntity.getOrganization().getId(), false);
        newEntity.setOrganization(organization);
        newEntity.setSaltedPassword(passwordEncoder.encode(newEntity.getSaltedPassword()));
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
