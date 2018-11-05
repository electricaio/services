package io.electrica.user.service;

import com.google.common.collect.Sets;
import io.electrica.common.helper.AuthorityHelper;
import io.electrica.common.helper.TokenHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static io.electrica.common.helper.AuthorityConstants.*;

@Component
public class AccessKeyGenerator {

    private final AuthorizationServerTokenServices tokenServices;

    @Inject
    public AccessKeyGenerator(AuthorizationServerTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }

    Key createAccessKey(Long userId, Long accessKeyId) {
        OAuth2AccessToken accessToken = createAccessToken(
                userId,
                ACCESS_KEY_CLIENT_ID,
                AuthorityHelper.buildGrantedAuthoritiesForAccessKey(accessKeyId),
                Sets.newHashSet(
                        READ_SCOPE,
                        SDK_SCOPE
                ),
                Sets.newHashSet(
                        USER_SERVICE_RESOURCE_ID,
                        CONNECTOR_HUB_SERVICE_RESOURCE_ID,
                        INVOKER_SERVICE_RESOURCE_ID
                )
        );
        Object jti = accessToken.getAdditionalInformation().get(TokenHelper.JWT_TOKEN_IDENTIFIER);
        checkArgument(jti != null, "Token identifier required");
        return new Key(
                UUID.fromString(jti.toString()),
                accessToken.getValue()
        );
    }

    private OAuth2AccessToken createAccessToken(
            Long userId,
            String clientId,
            @Nullable Collection<? extends GrantedAuthority> authorities,
            Set<String> scope,
            @Nullable Set<String> resourceIds

    ) {
        String tokenUsername = TokenHelper.buildIdTokenUsername(userId);
        OAuth2Authentication authenticationRequest = new OAuth2Authentication(
                new OAuth2Request(
                        Collections.emptyMap(),
                        clientId,
                        authorities,
                        false,
                        scope,
                        resourceIds,
                        null,
                        null,
                        null
                ),
                new UsernamePasswordAuthenticationToken(
                        new User(
                                tokenUsername,
                                "",
                                true,
                                true,
                                true,
                                true,
                                authorities == null ? Collections.emptyList() : authorities
                        ),
                        null,
                        authorities
                ));
        authenticationRequest.setAuthenticated(true);

        return tokenServices.createAccessToken(authenticationRequest);
    }

    @Getter
    @AllArgsConstructor
    static class Key {
        private final UUID jti;
        private final String value;
    }
}
