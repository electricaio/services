package io.electrica.common.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * A chain security expression handler. Can handle:
 * <ol>
 * <li>default method security expressions</li>
 * <li>the set provided by {@link OAuth2SecurityExpressionMethods} using the 'oauth2' namespace to access the
 * methods</li>
 * <li>another sets provided in container using {@link ExpressionMethodsFactory}</li>
 * </ol>
 */
@Component
public class ChainExpressionHandler extends OAuth2MethodSecurityExpressionHandler {

    private final List<ExpressionMethodsFactory> factories;

    @Inject
    public ChainExpressionHandler(List<ExpressionMethodsFactory> factories) {
        this.factories = factories;
        setDefaultRolePrefix("");
    }

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(
            Authentication authentication,
            MethodInvocation mi
    ) {
        StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, mi);
        for (ExpressionMethodsFactory factory : factories) {
            ec.setVariable(factory.getNamespace(), factory.create(authentication, mi));
        }
        return ec;
    }

}
