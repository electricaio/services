package io.electrica.migration.common;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;
import io.electrica.connector.hub.service.dto.ConnectorDtoService;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class V0_0_1_020__Seed_Echo_Test_v1_connector implements SpringJdbcMigration {

    private static final String CONNECTOR_TYPE = "Foundation";

    private static ConnectorType getConnectorType(ConnectorTypeRepository connectorTypeRepository) {
        return connectorTypeRepository.findAll()
                .stream()
                .filter(st -> st.getName().equals(CONNECTOR_TYPE))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Connector type with passed name does not exist."));
    }

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();
        ConnectorDtoService connectorDtoService = context.getBean(ConnectorDtoService.class);
        ConnectorTypeRepository connectorTypeRepository = context.getBean(ConnectorTypeRepository.class);

        ConnectorType connectorType = getConnectorType(connectorTypeRepository);
        CreateConnectorDto connector = new CreateConnectorDto(
                connectorType.getId(),
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
        connectorDtoService.create(connector);
    }
}
