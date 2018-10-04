package io.electrica.common.config;

import io.electrica.common.security.ChainExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Enable annotated method security and customise it with chain expression handler configuration.
 *
 * @see ChainExpressionHandler
 * @see org.springframework.security.access.prepost.PreAuthorize
 * @see org.springframework.security.access.prepost.PostAuthorize
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private ChainExpressionHandler chainExpressionHandler;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return chainExpressionHandler;
    }
}
