package io.electrica.common.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Component that register {@link CommonExpressionMethods} factory in 'common' namespace.
 */
@Component
public class CommonExpressionMethodsFactory implements ExpressionMethodsFactory {

    @Override
    public String getNamespace() {
        return "common";
    }

    @Override
    public Object create(Authentication authentication, MethodInvocation mi) {
        return new CommonExpressionMethods(authentication);
    }
}
