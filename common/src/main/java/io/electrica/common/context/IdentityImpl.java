package io.electrica.common.context;

import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.helper.ValueCache;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * Identity implementation for authenticated users.
 */
public class IdentityImpl implements Identity {

    private final Authentication authentication;

    private final ValueCache<Long> organizationId = new ValueCache<>(() ->
            AuthorityHelper.readOrganizationId(getAuthentication().getAuthorities())
    );
    private final ValueCache<Long> accessKeyId = new ValueCache<>(() ->
            AuthorityHelper.readAccessKeyId(getAuthentication().getAuthorities())
    );
    private final ValueCache<Long> userId = new ValueCache<>(() ->
            TokenHelper.extractIdFromTokenUsername(getAuthentication().getName())
    );
    private final ValueCache<Set<RoleType>> roles = new ValueCache<>(() ->
            unmodifiableSet(AuthorityHelper.readRoles(getAuthentication().getAuthorities()))
    );
    private final ValueCache<Set<PermissionType>> permissions = new ValueCache<>(() ->
            unmodifiableSet(AuthorityHelper.readPermissions(getAuthentication().getAuthorities()))
    );
    private final ValueCache<String> token = new ValueCache<>(() -> {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) getAuthentication().getDetails();
        return details.getTokenValue();
    });
    private final ValueCache<Map<String, Object>> tokenClaims = new ValueCache<>(() ->
            unmodifiableMap(TokenHelper.getClaims(getToken()))
    );
    private final ValueCache<Set<String>> oauthScopes = new ValueCache<>(() ->
            ((OAuth2Authentication) getAuthentication()).getOAuth2Request().getScope()
    );

    public IdentityImpl(Authentication authentication) {
        this.authentication = requireNonNull(authentication);
    }

    @Override
    public long getUserId() {
        return userId.get();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getToken() {
        return token.get();
    }

    @Override
    public UUID getTokenJti() {
        return TokenHelper.getJti(tokenClaims.get());
    }

    @Override
    public Long getAccessKeyId() {
        return accessKeyId.get();
    }

    @Override
    public long getTokenIssuedAt() {
        return TokenHelper.getIssuedAt(tokenClaims.get());
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public Set<RoleType> getRoles() {
        return roles.get();
    }

    @Override
    public Set<PermissionType> getPermissions() {
        return permissions.get();
    }

    @Override
    public long getOrganizationId() {
        return organizationId.get();
    }

    @Override
    public Set<String> getOauthScopes() {
        return oauthScopes.get();
    }
}
