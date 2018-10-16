package io.electrica.migration;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.user.model.Permission;
import io.electrica.user.model.Role;
import io.electrica.user.model.RoleToPermission;
import io.electrica.user.repository.PermissionRepository;
import io.electrica.user.repository.RoleRepository;
import io.electrica.user.repository.RoleToPermissionRepository;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class V0_0_1__01_Main_37_Seed_default_roles_and_permissions implements SpringJdbcMigration {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private RoleToPermissionRepository roleToPermissionRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();

        roleRepository = context.getBean(RoleRepository.class);
        permissionRepository = context.getBean(PermissionRepository.class);
        roleToPermissionRepository = context.getBean(RoleToPermissionRepository.class);

        Role orgUser = saveRole(RoleType.OrgUser, "Organization user", "Restricted to self entities within organization");
        Role orgAdmin = saveRole(RoleType.OrgAdmin, "Organization admin", "Restricted to self organization");
        Role superAdmin = saveRole(RoleType.SuperAdmin, "Super admin", "Can change any organization");

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
        Permission createSTL = savePermission(PermissionType.CreateSTL, "Create STL", "CreateSTL");
        Permission stlDeActivate = savePermission(PermissionType.STLDeActivate, "Disable STL", "STLDeActivate");
        Permission stlActivate = savePermission(PermissionType.STLActivate, "Activate STL", "STLActivate");
        Permission associateAccessKeyToSTL = savePermission(PermissionType.AssociateAccessKeyToSTL, "Associate access key to STL", "AssociateAccessKeyToSTL");
        Permission listActiveSTLs = savePermission(PermissionType.ListActiveSTLs, "List active STLs", "ListActiveSTLs");
        Permission addPermission = savePermission(PermissionType.AddPermission, "Add permission", "AddPermission");
        Permission removePermission = savePermission(PermissionType.RemovePermission, "Remove permission", "RemovePermission");

        assignPermissionsToRole(orgUser,
                updateUser,
                deleteUser,
                readUser,
                readOrg,
                createAccessKey,
                associateAccessKeyToSTL,
                listActiveSTLs
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
        for (Permission permission : permissions) {
            RoleToPermission rp = new RoleToPermission();
            rp.setRole(role);
            rp.setPermission(permission);

            roleToPermissionRepository.save(rp);
        }
    }

    private Permission savePermission(PermissionType type, String name, String description) {
        Permission p = new Permission();
        p.setType(type);
        p.setName(name);
        p.setDescription(description);

        return permissionRepository.save(p);

    }

    private Role saveRole(RoleType type, String name, String description) {
        Role r = new Role();
        r.setType(type);
        r.setName(name);
        r.setDescription(description);
        return roleRepository.save(r);
    }
}
