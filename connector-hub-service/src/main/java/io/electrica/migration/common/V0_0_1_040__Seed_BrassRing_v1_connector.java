package io.electrica.migration.common;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.migration.MigrationUtils;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class V0_0_1_040__Seed_BrassRing_v1_connector implements SpringJdbcMigration {

    private static final Map<String, String> PROPERTIES = new HashMap<String, String>() {{
        put("http-client.max-idle-connections", "10");
        put("http-client.keep-alive-duration-min", "60");
    }};

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();
        CreateConnectorDto connector = new CreateConnectorDto(
                null,
                AuthorizationType.Token,
                "BrassRing Applications",
                "BrassRing Applications Connector",
                "brassring",
                "applications",
                "1",
                "https://www.electrica.io",
                "https://www.ibm.com/in-en/talent-management",
                "https://www.electrica.io",
                "https://developer.ibm.com/answers/storage/attachments/19160-brassring-logo.png",
                PROPERTIES
        );
        MigrationUtils.saveConnector(context, connector, "Talent");
    }
}
