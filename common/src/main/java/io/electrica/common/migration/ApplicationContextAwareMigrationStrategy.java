package io.electrica.common.migration;

import io.electrica.common.EnvironmentType;
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
    private final EnvironmentType environmentType;

    @Inject
    public ApplicationContextAwareMigrationStrategy(ApplicationContext applicationContext,
                                                    EnvironmentType environmentType) {
        this.applicationContext = applicationContext;
        this.environmentType = environmentType;
    }

    @Override
    public void migrate(Flyway flyway) {
        FlywayApplicationContextBridge.setApplicationContext(applicationContext);

        switch (environmentType) {
            case Development:
                flyway.setLocations("/io/electrica/migration/common", "/io/electrica/migration/dev");
                break;
            case Default:
                flyway.setLocations("/io/electrica/migration/common");
                break;
            case Test:
                flyway.setLocations("/io/electrica/migration/common");
                break;
            default:
                throw new RuntimeException("Environment type " + environmentType +
                        " is not configured for data migration");
        }
        flyway.migrate();
    }
}
