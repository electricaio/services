package io.electrica.common.migration;

import io.electrica.common.EnvironmentType;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to order migration after application context initialization.
 * After initialization, prepare application context bridge for scripts.
 */
@Component
public class ApplicationContextAwareMigrationStrategy implements FlywayMigrationStrategy {

    private static final String MIGRATION_PACKAGE = "/io/electrica/migration";

    private final ApplicationContext applicationContext;
    private final String[] locations;

    @Inject
    public ApplicationContextAwareMigrationStrategy(
            ApplicationContext applicationContext,
            EnvironmentType environmentType
    ) {
        this.applicationContext = applicationContext;
        this.locations = buildLocations(environmentType);
    }

    private static String[] buildLocations(EnvironmentType environmentType) {
        List<String> locations = new ArrayList<>();
        locations.add(MIGRATION_PACKAGE + "/common");
        switch (environmentType) {
            case Default:
            case Development:
                locations.add(MIGRATION_PACKAGE + "/dev");
                break;
            case Test:
                locations.add(MIGRATION_PACKAGE + "/test");
                break;
            case Stage:
                locations.add(MIGRATION_PACKAGE + "/stage");
                break;
            case Production:
                locations.add(MIGRATION_PACKAGE + "/prod");
                break;
            default:
                throw new UnsupportedOperationException("Environment type is not configured for data migration: "
                        + environmentType);
        }
        return locations.toArray(new String[0]);
    }

    @Override
    public void migrate(Flyway flyway) {
        FlywayApplicationContextBridge.instance().setApplicationContext(applicationContext);
        flyway.setLocations(locations);
        flyway.migrate();
    }
}
