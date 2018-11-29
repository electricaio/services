package io.electrica.common.server;

import io.electrica.common.helper.AuthorityConstants;
import io.electrica.common.helper.RequestHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

/**
 * Enable and configure oauth2 Resource Server.
 */
@Component
@EnableResourceServer
public class ResourceServerConfigurerAdapterImpl extends ResourceServerConfigurerAdapter {

    private final String applicationName;

    @Inject
    public ResourceServerConfigurerAdapterImpl(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // specify id of resource, that validated using JWT token
        // token have to be explicitly granted to all required micro-services
        String resourceId;
        switch (applicationName) {
            case "user-service":
                resourceId = AuthorityConstants.USER_SERVICE_RESOURCE_ID;
                break;
            case "connector-hub-service":
                resourceId = AuthorityConstants.CONNECTOR_HUB_SERVICE_RESOURCE_ID;
                break;
            case "invoker-service":
                resourceId = AuthorityConstants.INVOKER_SERVICE_RESOURCE_ID;
                break;
            case "connector-service":
                resourceId = AuthorityConstants.CONNECTOR_SERVICE_RESOURCE_ID;
                break;
            case "webhook-service":
                resourceId = AuthorityConstants.WEBHOOK_SERVICE_RESOURCE_ID;
                break;
            case "websocket-service":
                resourceId = AuthorityConstants.WEBSOCKET_SERVICE_RESOURCE_ID;
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unknown service name to resolve oauth resourceId: " + applicationName
                );
        }
        resources.resourceId(resourceId);
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .requestMatchers(RequestHelper::isSecuredResource)
                .authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credentials"));
    }

}
