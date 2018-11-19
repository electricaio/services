package io.electrica.auth.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

import static io.electrica.common.helper.AuthorityConstants.*;

/**
 * AuthorizationServerConfigurerAdapterImpl provides implementation for Authorization server.
 * <p>
 * Please see the {@link io.electrica.UserServiceApplication} class for true identity
 */
@Component
@EnableAuthorizationServer
public class AuthorizationServerConfigurerAdapterImpl extends AuthorizationServerConfigurerAdapter {

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter tokenEnhancer;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Inject
    public AuthorizationServerConfigurerAdapterImpl(
            TokenStore tokenStore,
            JwtAccessTokenConverter tokenEnhancer,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager
    ) {
        this.tokenStore = tokenStore;
        this.tokenEnhancer = tokenEnhancer;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(FRONTEND_CLIENT_ID)
                // ToDo change secret
                .secret(passwordEncoder.encode("change_me"))
                .authorizedGrantTypes("refresh_token", "password")
                .resourceIds(USER_SERVICE_RESOURCE_ID, CONNECTOR_HUB_SERVICE_RESOURCE_ID)
                .scopes(CREATE_SCOPE, READ_SCOPE, UPDATE_SCOPE, DELETE_SCOPE, DO_SCOPE)
                .accessTokenValiditySeconds(30 * 60)
                .refreshTokenValiditySeconds(30 * 24 * 60 * 60)

                .and()
                .withClient(ACCESS_KEY_CLIENT_ID)
                .secret("{none}" + UUID.randomUUID())
                .resourceIds(ACCESS_KEY_CLIENT_RESOURCE_IDS.toArray(new String[0]))
                .scopes(ACCESS_KEY_CLIENT_SCOPES.toArray(new String[0]))
                .accessTokenValiditySeconds(Integer.MAX_VALUE);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .tokenEnhancer(tokenEnhancer)
                .reuseRefreshTokens(false) // TODO ugly hack - see token store, need to verify refresh token issued at
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .accessDeniedHandler(oauthAccessDeniedHandler())
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
        return new OAuth2AccessDeniedHandler();
    }

}
