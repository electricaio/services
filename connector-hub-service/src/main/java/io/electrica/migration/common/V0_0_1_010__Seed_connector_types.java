package io.electrica.migration.common;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

public class V0_0_1_010__Seed_connector_types implements SpringJdbcMigration {

    private ConnectorTypeRepository connectorTypeRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();

        connectorTypeRepository = context.getBean(ConnectorTypeRepository.class);
        connectorTypeRepository.saveAll(Arrays.asList(
                new ConnectorType("Foundation"),
                new ConnectorType("CRM"),
                new ConnectorType("Talent")
        ));
    }
}
