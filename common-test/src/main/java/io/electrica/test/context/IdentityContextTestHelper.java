package io.electrica.test.context;

import com.google.common.collect.Sets;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.helper.AuthorityConstants;
import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    static Authentication createAuthentication(
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

    static Authentication createAccessKeyAuthentication(long userId, Long accessKeyId, Long organizationId) {
        return createAuthentication(
                userId,
                AuthorityConstants.ACCESS_KEY_CLIENT_ID,
                Sets.newHashSet(READ_SCOPE, SDK_SCOPE),
                AuthorityHelper.buildGrantedAuthoritiesForAccessKey(accessKeyId, organizationId)
        );
    }

    @SneakyThrows
    public static void executeForUser(
            IdentityContextHolder identityContextHolder,
            long userId,
            long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions,
            Runnable work
    ) {
        Authentication authentication = IdentityContextTestHelper.createAuthentication(
                userId,
                organizationId,
                roles,
                permissions
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            IdentityImpl identity = new IdentityImpl(authentication);
            identityContextHolder.executeWithContext(identity, work::run);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    @SneakyThrows
    public static void executeForAccessKey(
            IdentityContextHolder identityContextHolder,
            long userId,
            long accessKeyId,
            Runnable work
    ) {
        Authentication authentication = IdentityContextTestHelper.createAccessKeyAuthentication(
                userId, accessKeyId, 0L
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            IdentityImpl identity = new IdentityImpl(authentication);
            identityContextHolder.executeWithContext(identity, work::run);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

}
