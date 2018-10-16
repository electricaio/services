package io.electrica.test.context;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow to specify user information for test methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForUser {

    long DEFAULT_USER_ID = 0L;
    long DEFAULT_ORGANIZATION_ID = 0L;

    long userId() default DEFAULT_USER_ID;

    long organizationId() default DEFAULT_ORGANIZATION_ID;

    RoleType[] roles() default {};

    PermissionType[] permissions() default {};

}
