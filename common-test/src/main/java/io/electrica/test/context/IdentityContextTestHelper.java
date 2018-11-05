package io.electrica.test.context;

import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class IdentityContextTestHelper {

    private IdentityContextTestHelper() {
    }

    private static Authentication createAuthentication(
            long userId,
            Collection<? extends GrantedAuthority> authorities
    ) {
        String tokenUsername = TokenHelper.buildIdTokenUsername(userId);
        return new UsernamePasswordAuthenticationToken(tokenUsername, null, authorities);
    }

    public static Authentication createAuthentication(
            long userId,
            Long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions
    ) {
        Collection<? extends GrantedAuthority> authorities =
                AuthorityHelper.buildGrantedAuthorities(organizationId, roles, permissions);
        return createAuthentication(userId, authorities);
    }

    public static Authentication createAccessKeyAuthentication(long userId, Long accessKeyId) {
        return createAuthentication(userId, AuthorityHelper.buildGrantedAuthoritiesForAccessKey(accessKeyId));
    }
}
