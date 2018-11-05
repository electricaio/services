package io.electrica.test.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForAccessKey {

    long DEFAULT_USER_ID = 0L;
    long DEFAULT_ACCESS_KEY_ID = 0L;

    long userId() default DEFAULT_USER_ID;

    long accessKeyId() default DEFAULT_ACCESS_KEY_ID;

}
