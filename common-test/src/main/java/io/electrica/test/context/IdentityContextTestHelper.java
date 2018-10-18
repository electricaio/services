package io.electrica.test.context;

import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.mockito.Mockito.*;

public class IdentityContextTestHelper {

    private IdentityContextTestHelper() {
    }

    public static Authentication createAuthentication(
            long userId,
            Long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions
    ) {
        Authentication authentication = mock(Authentication.class);

        String tokenUsername = TokenHelper.buildIdTokenUsername(userId);
        when(authentication.getName()).thenReturn(tokenUsername);

        Collection<? extends GrantedAuthority> authorities =
                AuthorityHelper.buildGrantedAuthorities(organizationId, roles, permissions);
        doReturn(authorities).when(authentication).getAuthorities();

        return authentication;
    }
}
