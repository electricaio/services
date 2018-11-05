package io.electrica.common.context;

import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.helper.ValueCache;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Identity implementation for authenticated users.
 */
public class IdentityImpl implements Identity {

    private final Authentication authentication;
    private final Set<RoleType> roles;
    private final Set<PermissionType> permissions;
    private final ValueCache<Long> organizationId = new ValueCache<>(() ->
            AuthorityHelper.readOrganization(getAuthentication().getAuthorities())
    );
    private final ValueCache<Long> accessKeyId = new ValueCache<>(() ->
            AuthorityHelper.readAccessKeyId(getAuthentication().getAuthorities())
    );
    private final ValueCache<Map<String, String>> tokenClaims = new ValueCache<>(() ->
            TokenHelper.getClaims(getToken())
    );

    public IdentityImpl(Authentication authentication) {
        this.authentication = requireNonNull(authentication);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        roles = AuthorityHelper.readRoles(authorities);
        permissions = AuthorityHelper.readPermissions(authorities);
    }

    @Override
    public long getUserId() {
        return TokenHelper.extractIdFromTokenUsername(authentication.getName());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getToken() {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) getAuthentication().getDetails();
        return details.getTokenValue();
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
        return roles;
    }

    @Override
    public Set<PermissionType> getPermissions() {
        return permissions;
    }

    @Override
    public long getOrganizationId() {
        return organizationId.get();
    }
}
