package io.electrica.common.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.helper.TokenHelper;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Most common method security set used for custom security verifications.
 */
public class CommonExpressionMethods {

    private final Authentication authentication;
    private final MethodInvocation mi;

    private CommonExpressionMethods(Authentication authentication, MethodInvocation mi) {
        this.authentication = authentication;
        this.mi = mi;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public boolean hasRole(String roleType) {
        return getIdentity().getRoles().contains(RoleType.valueOf(roleType));
    }

    public boolean haveOneOfRoles(String... roles) {
        boolean result = false;
        for (String role : roles) {
            if (hasRole(role)) {
                result = true;
                break;
            }
        }
        return result;
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

    public boolean isUser(Long userId) {
        return Objects.equals(getIdentity().getUserId(), userId);
    }

    public boolean hasPermission(String permission) {
        return getIdentity().getPermissions().contains(PermissionType.valueOf(permission));
    }

    public boolean userInOrganization(Long organizationId) {
        return Objects.equals(getIdentity().getOrganizationId(), organizationId);
    }

    /**
     * Component that register {@link CommonExpressionMethods} factory in 'common' namespace.
     */
    @Component
    public static class Factory implements ExpressionMethodsFactory {

        @Override
        public String getNamespace() {
            return "common";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new CommonExpressionMethods(authentication, mi);
        }
    }

}
