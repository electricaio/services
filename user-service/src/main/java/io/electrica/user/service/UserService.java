package io.electrica.user.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Organization;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User Service implementation class for managing users.
 */
@Component
public class UserService extends AbstractService<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    protected User executeCreate(User newEntity) {
        Long organizationId = newEntity.getOrganization().getId();
        if (organizationId != null) {
            Organization organization = getReference(Organization.class, organizationId);
            newEntity.setOrganization(organization);
        } else {
            throw new IllegalArgumentException("Required field: organization.id");
        }

        newEntity.setSaltedPassword(passwordEncoder.encode(newEntity.getSaltedPassword()));
        return userRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(User merged, User update) {
        throw new UnsupportedOperationException();
    }

    public List<User> getUsersForOrg(Long orgId) {
        return userRepository.findByOrganizationId(orgId);
    }


    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
}
