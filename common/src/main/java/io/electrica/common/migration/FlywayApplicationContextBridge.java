package io.electrica.common.migration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

public class FlywayApplicationContextBridge {

    private static final FlywayApplicationContextBridge INSTANCE = new FlywayApplicationContextBridge();

    private final AtomicReference<ApplicationContext> applicationContext = new AtomicReference<>();

    public static FlywayApplicationContextBridge instance() {
        return INSTANCE;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext.get();
    }

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext.set(applicationContext);
    }

}
