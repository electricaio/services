package io.electrica.common.context;

import io.electrica.common.helper.TokenHelper;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import static java.util.Objects.requireNonNull;

/**
 * Identity implementation for authenticated users.
 */
public class IdentityImpl implements Identity {

    private final Authentication authentication;

    public IdentityImpl(Authentication authentication) {
        this.authentication = requireNonNull(authentication);
    }

    @Override
    public int getUserId() {
        return TokenHelper.idFromTokenUsername(authentication.getName());
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
}
