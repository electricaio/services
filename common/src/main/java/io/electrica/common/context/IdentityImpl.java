package io.electrica.common.context;

import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Identity implementation for authenticated users.
 */
public class IdentityImpl implements Identity {

    private final Authentication authentication;
    private final Set<PermissionType> permissions;
    private final long organizationId;

    public IdentityImpl(Authentication authentication) {
        this.authentication = requireNonNull(authentication);
        permissions = AuthorityHelper.readPermissions(authentication.getAuthorities());
        organizationId = AuthorityHelper.readOrganization(authentication.getAuthorities());
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
    @SneakyThrows
    public long getTokenIssuedAt() {
        return TokenHelper.getIssuedAt(getToken());
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public Set<PermissionType> getPermissions() {
        return permissions;
    }

    @Override
    public long getOrganizationId() {
        return organizationId;
    }
}
