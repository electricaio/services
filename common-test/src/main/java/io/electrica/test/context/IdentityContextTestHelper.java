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

    public static Authentication createAuthentication(
            long userId,
            Long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions
    ) {
        String tokenUsername = TokenHelper.buildIdTokenUsername(userId);
        Collection<? extends GrantedAuthority> authorities =
                AuthorityHelper.buildGrantedAuthorities(organizationId, roles, permissions);
        return new UsernamePasswordAuthenticationToken(tokenUsername, null, authorities);
    }
}
