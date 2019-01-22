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

public class V0_0_1_050__Seed_HackerRank_Test_v3_connector implements SpringJdbcMigration {

    private static final Map<String, String> PROPERTIES = new HashMap<String, String>() {{
        put("api.url", "https://www.hackerrank.com/x/api/v1/tests");
        put("http-client.max-idle-connections", "10");
        put("http-client.keep-alive-duration-min", "60");
    }};
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();
        CreateConnectorDto connector = new CreateConnectorDto(
                null,
                AuthorizationType.Token,
                "HackerRank for Work tests v3",
                "The HackerRank Tests Connector enables you to fetch and create tests on the HackerRank platform",
                "hackerrank-v3",
                "tests",
                "1",
                "https://www.electrica.io",
                "https://www.hackerrank.com",
                "https://www.hackerrank.com/work/apidocs#!/Tests/options_tests",
                "https://images.electrica.io/hackerrank-logo.png",
                PROPERTIES
        );

        MigrationUtils.saveConnector(context, connector, "Talent");
    }
}
