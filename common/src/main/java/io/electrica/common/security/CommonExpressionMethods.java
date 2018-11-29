package io.electrica.common.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.helper.ValueCache;
import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Set;

/**
 * Most common method security set used for custom security verifications.
 */
public class CommonExpressionMethods {

    private final Authentication authentication;
    private final ValueCache<Identity> identityCache = new ValueCache<>(this::createIdentity);

    protected CommonExpressionMethods(Authentication authentication) {
        this.authentication = authentication;
    }


    private Identity createIdentity() {
        return new IdentityImpl(authentication);
    }

    public Identity getIdentity() {
        return identityCache.get();
    }


    public long getUserId() {
        return getIdentity().getUserId();
    }

    public boolean isUser(Long userId) {
        return Objects.equals(getUserId(), userId);
    }

    public long getOrganizationId() {
        return getIdentity().getOrganizationId();
    }

    public boolean userInOrganization(Long organizationId) {
        return Objects.equals(getOrganizationId(), organizationId);
    }


    public Set<RoleType> getRoles() {
        return getIdentity().getRoles();
    }

    public boolean hasRole(String role) {
        RoleType roleType = RoleType.valueOf(role);
        return getRoles().contains(roleType);
    }

    public boolean haveOneOfRoles(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSuperAdmin() {
        return hasRole("SuperAdmin");
    }

    public boolean isOrgAdmin() {
        return hasRole("OrgAdmin");
    }

    public boolean isOrgUser() {
        return hasRole("OrgUser");
    }


    public Set<PermissionType> getPermissions() {
        return getIdentity().getPermissions();
    }

    public boolean hasPermission(String permission) {
        PermissionType permissionType = PermissionType.valueOf(permission);
        return getPermissions().contains(permissionType);
    }

}
