package io.electrica.common.migration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Used to order migration after application context initialization.
 * After initialization, prepare application context bridge for scripts.
 */
@Component
public class ApplicationContextAwareMigrationStrategy implements FlywayMigrationStrategy {

    private final ApplicationContext applicationContext;

    @Inject
    public ApplicationContextAwareMigrationStrategy(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void migrate(Flyway flyway) {
        FlywayApplicationContextBridge.setApplicationContext(applicationContext);
        flyway.migrate();
    }
}
