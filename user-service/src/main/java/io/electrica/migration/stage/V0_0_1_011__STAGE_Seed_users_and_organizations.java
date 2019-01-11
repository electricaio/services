package io.electrica.migration.stage;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.common.security.RoleType;
import io.electrica.user.model.Organization;
import io.electrica.user.model.Role;
import io.electrica.user.model.User;
import io.electrica.user.model.UserToRole;
import io.electrica.user.repository.OrganizationRepository;
import io.electrica.user.repository.RoleRepository;
import io.electrica.user.repository.UserRepository;
import io.electrica.user.repository.UserToRoleRepository;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class V0_0_1_011__STAGE_Seed_users_and_organizations implements SpringJdbcMigration {

    private RoleRepository roleRepository;
    private OrganizationRepository organizationRepository;
    private UserRepository userRepository;
    private UserToRoleRepository userToRoleRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();

        roleRepository = context.getBean(RoleRepository.class);
        organizationRepository = context.getBean(OrganizationRepository.class);
        userRepository = context.getBean(UserRepository.class);
        userToRoleRepository = context.getBean(UserToRoleRepository.class);

        Organization electricaOrg = saveOrganization("electrica.io");

        saveUser(
                "admin",
                "admin@electrica.io",
                "{bcrypt}$2a$10$htfqFCv6Vj.CYRFSCoOj4.fTR6pSLJPo9TYqFsUrlYYrjjKuGkmZ6",
                electricaOrg,
                RoleType.SuperAdmin
        );
        saveUser(
                "sandbox",
                "sandbox@electrica.io",
                "{bcrypt}$2a$10$2dK7d3rvD5cY/rdX8TzwweqqNFY/urATZAKZW.rUOGUqR4kONV5Ii",
                electricaOrg,
                RoleType.OrgUser
        );
        saveUser(
                "test",
                "test@electrica.io",
                "{bcrypt}$2a$10$MVR5uT4vEHxs1lGWL4asfOCqp2dIbI4mbptwONn.H3OpwycN.7N3O",
                electricaOrg,
                RoleType.OrgUser
        );
    }

    private Organization saveOrganization(String name) {
        Organization o = new Organization();
        o.setName(name);
        return organizationRepository.save(o);
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private void saveUser(String username, String email, String passwordHash, Organization org, RoleType roleType) {
        User user = new User();
        user.setOrganization(org);
        user.setFirstName(username);
        user.setEmail(email);
        user.setLastName(username);
        user.setSaltedPassword(passwordHash);

        user = userRepository.save(user);
        Role role = roleRepository.findOneByType(roleType).orElseThrow(() ->
                new IllegalArgumentException("Role not found: " + roleType)
        );

        UserToRole userToRole = new UserToRole();
        userToRole.setUser(user);
        userToRole.setRole(role);
        userToRoleRepository.save(userToRole);
    }
}
