package io.electrica.common.migration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

public class FlywayApplicationContextBridge {

    private static final AtomicReference<ApplicationContext> APPLICATION_CONTEXT = new AtomicReference<>();

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT.get();
    }

    static void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        APPLICATION_CONTEXT.set(applicationContext);
    }

}
