package io.electrica.migration.dev;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class V0_0_2__01_Main_38_Seed_User_Service_Data implements SpringJdbcMigration {

    private RoleRepository roleRepository;
    private OrganizationRepository organizationRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserToRoleRepository userToRoleRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();

        roleRepository = context.getBean(RoleRepository.class);
        organizationRepository = context.getBean(OrganizationRepository.class);
        userRepository = context.getBean(UserRepository.class);
        passwordEncoder = context.getBean(PasswordEncoder.class);
        userToRoleRepository = context.getBean(UserToRoleRepository.class);

        Organization defaultOrg = saveOrganization("default");
        Organization electricaOrg = saveOrganization("electrica.io");
        saveUser("admin", "admin@electrica.io", "admin", electricaOrg, RoleType.SuperAdmin);
        saveUser("scott", "scott@electrica.io", "scott", defaultOrg, RoleType.OrgUser);

    }

    private Organization saveOrganization(String name) {
        Organization o = new Organization();
        o.setName(name);
        return organizationRepository.save(o);
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "")
    private User saveUser(String username, String email, String password, Organization org, RoleType roleType) {
        User user = new User();
        user.setOrganization(org);
        user.setFirstName(username);
        user.setEmail(email);
        user.setLastName(username);
        user.setSaltedPassword(passwordEncoder.encode(password));
        User newEntity = userRepository.save(user);
        User userGet = userRepository.getOne(newEntity.getId());
        UserToRole userToRole = new UserToRole();
        userToRole.setUser(userGet);
        Role role = roleRepository.findOneByType(roleType).get();
        userToRole.setRole(role);
        userToRoleRepository.save(userToRole);
        return newEntity;
    }
}