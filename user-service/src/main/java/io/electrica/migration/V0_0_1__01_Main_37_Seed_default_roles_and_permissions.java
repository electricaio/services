package io.electrica.migration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.user.model.*;
import io.electrica.user.repository.*;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class V0_0_1__01_Main_37_Seed_default_roles_and_permissions implements SpringJdbcMigration {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private RoleToPermissionRepository roleToPermissionRepository;
    private OrganizationRepository organizationRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserToRoleRepository userToRoleRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();

        roleRepository = context.getBean(RoleRepository.class);
        permissionRepository = context.getBean(PermissionRepository.class);
        roleToPermissionRepository = context.getBean(RoleToPermissionRepository.class);
        organizationRepository = context.getBean(OrganizationRepository.class);
        userRepository = context.getBean(UserRepository.class);
        passwordEncoder = context.getBean(PasswordEncoder.class);
        userToRoleRepository = context.getBean(UserToRoleRepository.class);

        Organization defaultOrg = saveOrganization("default");
        Organization electricaOrg = saveOrganization("electrica.io");

        Role orgUser = saveRole(RoleType.OrgUser, "Organization user", "Restricted to self entities within organization");
        Role orgAdmin = saveRole(RoleType.OrgAdmin, "Organization admin", "Restricted to self organization");
        Role superAdmin = saveRole(RoleType.SuperAdmin, "Super admin", "Can change any organization");

        saveUser("admin", "admin@electrica.io", "sk8freee", electricaOrg, RoleType.SuperAdmin);
        saveUser("scott", "scott@electrica.io", "tiger", defaultOrg, RoleType.OrgUser);

        Permission updateUser = savePermission(PermissionType.UpdateUser, "Update user", "UpdateUser");
        Permission deleteUser = savePermission(PermissionType.DeleteUser, "Delete user", "DeleteUser");
        Permission createUser = savePermission(PermissionType.CreateUser, "Create user", "CreateUser");
        Permission readUser = savePermission(PermissionType.ReadUser, "Read user", "ReadUser");
        Permission createOrg = savePermission(PermissionType.CreateOrg, "Create organization", "CreateOrg");
        Permission readOrg = savePermission(PermissionType.ReadOrg, "Read organization", "ReadOrg");
        Permission addUserToOrg = savePermission(PermissionType.AddUserToOrg, "Add user to organization", "AddUserToOrg");
        Permission updateOrg = savePermission(PermissionType.UpdateOrg, "Update organization", "UpdateOrg");
        Permission activateOrg = savePermission(PermissionType.ActivateOrg, "Activate organization", "ActivateOrg");
        Permission createAccessKey = savePermission(PermissionType.CreateAccessKey, "Create access key", "CreateAccessKey");
        Permission readAccessKey = savePermission(PermissionType.ReadAccessKey, "Read access key", "ReadAccessKey");
        Permission deleteAccessKey = savePermission(PermissionType.DeleteAccessKey, "Delete access key", "DeleteAccessKey");
        Permission createSTL = savePermission(PermissionType.CreateConnector, "Create Connector", "CreateConnector");
        Permission stlDeActivate = savePermission(PermissionType.DeActivateConnection, "Deactivate Connection",
                "DeActivateConnection");
        Permission stlActivate = savePermission(PermissionType.ActivateConnection, "Activate Connection",
                "ActivateConnection");
        Permission associateAccessKeyToSTL = savePermission(PermissionType.AssociateAccessKeyToConnector, "Associate" +
                        " access key to Connector",
                "AssociateAccessKeyToConnector");
        Permission listActiveSTLs = savePermission(PermissionType.ReadActiveConnection, "Read active connections",
                "ReadActiveConnection");
        Permission addPermission = savePermission(PermissionType.AddPermission, "Add permission", "AddPermission");
        Permission removePermission = savePermission(PermissionType.RemovePermission, "Remove permission", "RemovePermission");
        Permission createWebhook = savePermission(PermissionType.CreateWebhook, "Create Webhook", "CreateWebhook");
        Permission deleteWebhook = savePermission(PermissionType.DeleteWebhook, "Delete Webhook", "DeleteWebhook");
        Permission readWebhook = savePermission(PermissionType.ReadWebhook, "Read Webhook", "ReadWebhook");

        assignPermissionsToRole(orgUser,
                updateUser,
                deleteUser,
                readUser,
                readOrg,
                createAccessKey,
                readAccessKey,
                deleteAccessKey,
                associateAccessKeyToSTL,
                listActiveSTLs,
                createWebhook,
                deleteWebhook,
                readWebhook
        );


        assignPermissionsToRole(orgAdmin,
                updateUser,
                deleteUser,
                readUser,
                createUser,
                readOrg,
                addUserToOrg,
                updateOrg,
                activateOrg,
                createAccessKey,
                readAccessKey,
                deleteAccessKey,
                listActiveSTLs,
                addPermission,
                removePermission
        );


        assignPermissionsToRole(superAdmin,
                updateUser,
                deleteUser,
                createUser,
                readUser,
                createOrg,
                readOrg,
                addUserToOrg,
                updateOrg,
                activateOrg,
                createAccessKey,
                readAccessKey,
                deleteAccessKey,
                createSTL,
                stlDeActivate,
                stlActivate,
                associateAccessKeyToSTL,
                listActiveSTLs,
                addPermission,
                removePermission
        );
    }

    private void assignPermissionsToRole(Role role, Permission... permissions) {
        Arrays.stream(permissions)
                .map(permission -> {
                    RoleToPermission rp = new RoleToPermission();
                    rp.setRole(role);
                    rp.setPermission(permission);
                    return rp;
                }).forEach(rtp -> roleToPermissionRepository.save(rtp));
    }

    private Permission savePermission(PermissionType type, String name, String description) {
        Permission p = new Permission();
        p.setType(type);
        p.setName(name);
        p.setDescription(description);

        return permissionRepository.save(p);

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

    private Role saveRole(RoleType type, String name, String description) {
        Role r = new Role();
        r.setType(type);
        r.setName(name);
        r.setDescription(description);
        return roleRepository.save(r);
    }
}
