package io.electrica.test.context;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class IdentityContextTestHelper {

    private IdentityContextTestHelper() {
    }

    public static Identity createIdentity(long userId, Long organizationId, Set<PermissionType> permissions) {
        Authentication authentication = mock(Authentication.class);

        String tokenUsername = TokenHelper.buildIdTokenUsername(userId);
        when(authentication.getName()).thenReturn(tokenUsername);

        String permissionsAuthority = AuthorityHelper.writePermissions(permissions);
        String organizationAuthority = AuthorityHelper.writeOrganization(organizationId);
        Collection<? extends GrantedAuthority> authorities = Stream.of(permissionsAuthority, organizationAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        doReturn(authorities).when(authentication).getAuthorities();

        return new IdentityImpl(authentication);
    }
}
