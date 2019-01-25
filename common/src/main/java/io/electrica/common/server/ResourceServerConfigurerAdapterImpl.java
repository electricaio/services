package io.electrica.common.server;

import com.google.common.collect.ImmutableMap;
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
import java.util.Map;

import static io.electrica.common.helper.AuthorityConstants.*;

/**
 * Enable and configure oauth2 Resource Server.
 */
@Component
@EnableResourceServer
public class ResourceServerConfigurerAdapterImpl extends ResourceServerConfigurerAdapter {

    private static final Map<String, String> APP_NAME_TO_RESOURCE_ID_MAP = ImmutableMap.<String, String>builder()
            .put("user-service", USER_SERVICE_RESOURCE_ID)
            .put("connector-hub-service", CONNECTOR_HUB_SERVICE_RESOURCE_ID)
            .put("invoker-service", INVOKER_SERVICE_RESOURCE_ID)
            .put("connector-service", CONNECTOR_SERVICE_RESOURCE_ID)
            .put("webhook-service", WEBHOOK_SERVICE_RESOURCE_ID)
            .put("websocket-service", WEBSOCKET_SERVICE_RESOURCE_ID)
            .put("it-service", INTEGRATION_TEST_SERVICE_RESOURCE_ID)
            .put("metric-service", METRIC_SERVICE_RESOURCE_ID)
            .build();

    private final String applicationName;

    // specify id of resource, that validated using JWT token
    // token have to be explicitly granted to all required micro-services
    private final String resourceId;

    @Inject
    public ResourceServerConfigurerAdapterImpl(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
        this.resourceId = APP_NAME_TO_RESOURCE_ID_MAP.getOrDefault(applicationName, null);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        if (resourceId == null) {
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
