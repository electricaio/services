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

    Map<String, String> SLACK_PROPERTIES = new HashMap<String, String>() {{
        put("send-message.url-template", "https://hooks.slack.com/services/%s");
        put("http-client.max-idle-connections", "10");
        put("http-client.keep-alive-duration-min", "60");
    }};

    Map<String, String> TEST_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();
        connectorDtoService = context.getBean(ConnectorDtoService.class);

        createConnector("Slack_V1", AuthorizationType.Token, 3L, "slack", "channel", "v1", SLACK_PROPERTIES);
        createConnector("Slack_V2", AuthorizationType.Token, 3L, "slack", "channel", "v2", SLACK_PROPERTIES);
        createConnector("JDBC", AuthorizationType.Basic, 1L, "jdbc", "Applications", "v1");
        createConnector("SalesForce", AuthorizationType.Token, 2L, "salesforce", "Applications", "v1");
    }

    private void createConnector(String name, AuthorizationType authorizationType, Long typeId, String namespace,
                                 String resource, String version) {
        createConnector(name,authorizationType,typeId,namespace,resource,version,TEST_PROPERTIES);

    }

    private void createConnector(String name, AuthorizationType authorizationType, Long typeId, String namespace,
                                 String resource, String version,Map<String,String> properties) {
        CreateConnectorDto connectorDto = new CreateConnectorDto();
        connectorDto.setName(name);
        connectorDto.setAuthorizationType(authorizationType);
        connectorDto.setTypeId(typeId);
        connectorDto.setNamespace(namespace);
        connectorDto.setResource(resource);
        connectorDto.setVersion(version);
        connectorDto.setProperties(properties);
        connectorDtoService.create(connectorDto);

    }
}
