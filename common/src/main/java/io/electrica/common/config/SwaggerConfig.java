package io.electrica.common.config;

import io.electrica.common.EnvironmentType;
import io.electrica.common.helper.AuthorityConstants;
import io.electrica.common.helper.RequestHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String PASSWORD_SCHEME = "Electrica OAuth2 Password Client";

    @Inject
    @Value("${common.service.version}")
    private String version;

    @Inject
    private EnvironmentType environmentType;

    @Inject
    @Value("${common.swagger.host:}")
    private String swaggerHost;

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(input -> input != null &&
                        (environmentType.isSafe() || !RequestHelper.isPrivateApi(input)) &&
                        !RequestHelper.isAuthPath(input) &&
                        !RequestHelper.isErrorPath(input)
                )
                .build()
                .host(swaggerHost)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, Arrays.asList(
                        new ResponseMessageBuilder()
                                .code(500)
                                .message("500 message")
                                //.responseModel(new ModelRef("Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(403)
                                .message("Forbidden!")
                                .build()
                ))
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private SecurityScheme securityScheme() {
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant("/oauth/token");
        return new OAuthBuilder().name(PASSWORD_SCHEME)
                .grantTypes(Collections.singletonList(grantType))
                .scopes(Arrays.asList(scopes()))
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference(PASSWORD_SCHEME, scopes())))
                .forPaths(RequestHelper::isSecuredResource)
                .build();
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope(AuthorityConstants.CREATE_SCOPE, "for create operations"),
                new AuthorizationScope(AuthorityConstants.READ_SCOPE, "for read operations"),
                new AuthorizationScope(AuthorityConstants.UPDATE_SCOPE, "for update operations"),
                new AuthorizationScope(AuthorityConstants.DELETE_SCOPE, "for delete operations"),
                new AuthorizationScope(AuthorityConstants.DO_SCOPE, "for change state operations"),
                new AuthorizationScope(AuthorityConstants.SDK_SCOPE, "for SDK operations")
        };
    }

    private ApiInfo apiInfo() {
        String versionDescription = environmentType.isSafe() ? "-" + environmentType : "";
        return new ApiInfo(
                "Electrica REST API",
                "Documentation for Electrica public REST API services.",
                version + versionDescription,
                "https://electrica.io/terms-of-service",
                new Contact("Support", "https://electrica.io/support", "support@electrica.io"),
                null,
                null,
                Collections.emptyList()
        );
    }
}
