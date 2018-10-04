package io.electrica.common.server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.stereotype.Component;

/**
 * No-operation security configurer adapter that allow any requests because we use oauth2 Resource Server for security.
 * <p>
 * <p>
 * <b>Note:</b> condition on {@link WebSecurityConfigurerAdapter} required for Authorization Server implementation
 * because it have to use another configured one.
 *
 * @see EnableResourceServer
 * @see ResourceServerConfigurerAdapter
 */
@Component
@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
public class NopWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().anyRequest().permitAll();
    }
}
