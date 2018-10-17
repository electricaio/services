package io.electrica.user.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Organization;
import io.electrica.user.model.User;
import io.electrica.user.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<User> findByEmailWithPermissions(String email) {
        return userRepository.findOneByEmailIgnoreCase(email);
    }

    public Optional<User> findByIdWithPermissions(Long id) {
        return userRepository.findOneById(id);
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    protected User executeCreate(User newEntity) {
        Long organizationId = newEntity.getOrganization().getId();
        Organization organization = getReference(Organization.class, organizationId);
        newEntity.setOrganization(organization);

        newEntity.setSaltedPassword(passwordEncoder.encode(newEntity.getSaltedPassword()));
        return userRepository.save(newEntity);
    }

    @Override
    protected EntityValidator<User> getValidator() {
        return create -> {
            // ToDo validate email and password

            List<String> required = new ArrayList<>();

            Organization org = create.getOrganization();
            if (org == null) {
                required.add("organization");
            } else if (org.getId() == null) {
                required.add("organization.id");
            }

            if (!required.isEmpty()) {
                throw new IllegalArgumentException("Required fields: " + String.join(", ", required));
            }
        };
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
