package io.electrica.migration.common;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.migration.MigrationUtils;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class V0_0_1_020__Seed_Echo_Test_v1_connector implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();
        CreateConnectorDto connector = new CreateConnectorDto(
                null,
                AuthorizationType.None,
                "Echo Test v1",
                "Simple connector for test purposes, that allow you ping cluster and receive echo messages",
                "echo",
                "test",
                "1",
                "https://www.electrica.io",
                "https://www.electrica.io",
                "https://www.electrica.io",
                "https://d1yjjnpx0p53s8.cloudfront.net/styles/logo-original-577x577/s3/0022/0974/brand.gif",
                null
        );

        MigrationUtils.saveConnector(context, connector, "Foundation");
    }
}
