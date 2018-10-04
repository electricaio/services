package io.electrica.common.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;

/**
 * Factory interface to register method security set in specified namespace.
 */
public interface ExpressionMethodsFactory {

    String getNamespace();

    Object create(Authentication authentication, MethodInvocation mi);

}
