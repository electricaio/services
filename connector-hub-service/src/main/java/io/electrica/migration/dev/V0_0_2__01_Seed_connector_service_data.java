package io.electrica.migration.dev;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.service.dto.ConnectorDtoService;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class V0_0_2__01_Seed_connector_service_data implements SpringJdbcMigration {

    private ConnectorDtoService connectorDtoService;

    Map<String, String> TEST_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();
        connectorDtoService = context.getBean(ConnectorDtoService.class);

        createConnector("Slack", AuthorizationType.None, 3L, "slack", "Applications", "1.0");
        createConnector("JDBC", AuthorizationType.Basic, 1L, "jdbc", "Applications", "1.0");
        createConnector("SalesForce", AuthorizationType.Token, 2L, "salesforce", "Applications", "1.0");
    }

    private void createConnector(String name, AuthorizationType authorizationType, Long typeId, String namespace,
                                 String resource, String version) {
        CreateConnectorDto connectorDto = new CreateConnectorDto();
        connectorDto.setName(name);
        connectorDto.setAuthorizationType(authorizationType);
        connectorDto.setTypeId(typeId);
        connectorDto.setNamespace(namespace);
        connectorDto.setResource(resource);
        connectorDto.setVersion(version);
        connectorDto.setProperties(TEST_PROPERTIES);
        connectorDtoService.create(connectorDto);

    }
}
