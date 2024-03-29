package io.electrica.user;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommonTestModuleSupportTest extends UserServiceApplicationTest {

    @Inject
    private IdentityContextHolder identityContextHolder;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Test
    public void ddlAutoContextTest() {
        assertEquals("validate", ddlAuto);
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.ActivateOrg)
    public void annotationIdentityContextTest() {
        Identity identity = identityContextHolder.getIdentity();
        assertEquals(1, identity.getUserId());
        assertEquals(1, identity.getOrganizationId());
        assertEquals(EnumSet.of(RoleType.OrgUser), identity.getRoles());
        assertEquals(EnumSet.of(PermissionType.ActivateOrg), identity.getPermissions());
    }

    @Test
    public void noIdentityContextTest() {
        Identity identity = identityContextHolder.getIdentity();
        assertNull(identity);
    }

    @Test
    public void closureIdentityContextTest() {
        EnumSet<RoleType> expectedRoles = EnumSet.of(RoleType.SuperAdmin);
        EnumSet<PermissionType> expectedPermissions = EnumSet.of(PermissionType.AddPermission);
        executeForUser(2, 2, expectedRoles, expectedPermissions, () -> {
            Identity identity = identityContextHolder.getIdentity();
            assertEquals(2, identity.getUserId());
            assertEquals(2, identity.getOrganizationId());
            assertEquals(expectedRoles, identity.getRoles());
            assertEquals(expectedPermissions, identity.getPermissions());
        });
    }
}
