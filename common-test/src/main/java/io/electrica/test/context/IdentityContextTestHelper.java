package io.electrica.test.context;

import com.google.common.collect.Sets;
import io.electrica.common.helper.AuthorityConstants;
import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static io.electrica.common.helper.AuthorityConstants.*;

public class IdentityContextTestHelper {

    private IdentityContextTestHelper() {
    }

    private static Authentication createAuthentication(
            long userId,
            String clientId,
            Set<String> scopes,
            Collection<? extends GrantedAuthority> authorities
    ) {
        String tokenUsername = TokenHelper.buildIdTokenUsername(userId);
        return new OAuth2Authentication(
                new OAuth2Request(
                        Collections.emptyMap(),
                        clientId,
                        null,
                        true,
                        scopes,
                        null,
                        null,
                        null,
                        null
                ),
                new UsernamePasswordAuthenticationToken(tokenUsername, null, authorities)
        );
    }

    public static Authentication createAuthentication(
            long userId,
            Long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions
    ) {
        Collection<? extends GrantedAuthority> authorities =
                AuthorityHelper.buildGrantedAuthorities(organizationId, roles, permissions);
        return createAuthentication(
                userId,
                AuthorityConstants.FRONTEND_CLIENT_ID,
                Sets.newHashSet(READ_SCOPE, UPDATE_SCOPE, DELETE_SCOPE, DO_SCOPE),
                authorities
        );
    }

    public static Authentication createAccessKeyAuthentication(long userId, Long accessKeyId) {
        return createAuthentication(
                userId,
                AuthorityConstants.ACCESS_KEY_CLIENT_ID,
                Sets.newHashSet(READ_SCOPE, SDK_SCOPE),
                AuthorityHelper.buildGrantedAuthoritiesForAccessKey(accessKeyId)
        );
    }
}
