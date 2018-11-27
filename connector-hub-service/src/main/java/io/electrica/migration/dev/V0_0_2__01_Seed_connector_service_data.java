package io.electrica.migration.dev;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;
import io.electrica.connector.hub.service.dto.ConnectorDtoService;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class V0_0_2__01_Seed_connector_service_data implements SpringJdbcMigration {

    private ConnectorDtoService connectorDtoService;
    private ConnectorTypeRepository connectorTypeRepository;

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
        connectorTypeRepository = context.getBean(ConnectorTypeRepository.class);

        createSlackConnector("V1");
        createSlackConnector("V2");
        createJDBCConnector("V1");
        createSalesForceConnector("V1");
    }

    private void createSalesForceConnector(String version) {
        CreateConnectorDto dto = CreateConnectorDto.builder()
                .name("SalesForce_" + version).authorizationType(AuthorizationType.Token)
                .typeId(findConnectorType("CRM").getId()).namespace("salesforce").resource("applications")
                .version(version.toLowerCase()).properties(TEST_PROPERTIES).sourceUrl("https://localhost:8000/")
                .connectorUrl("https://localhost:8000/").sdkUrl("https://localhost:8000/")
                .imageUrl("https://localhost:8000/jdbc.png").description("test").build();

        connectorDtoService.create(dto);
    }

    private void createSlackConnector(String version) {
        CreateConnectorDto dto = CreateConnectorDto.builder()
                .name("Slack_" + version).authorizationType(AuthorizationType.Token)
                .typeId(findConnectorType("Talent").getId()).namespace("slack").resource("channel")
                .version(version.toLowerCase()).properties(SLACK_PROPERTIES).sourceUrl("https://api.slack.com/")
                .connectorUrl("https://api.slack.com/123/123").sdkUrl("https://localhost:8000/")
                .imageUrl("https://localhost:8000/slack.png").description("test").build();

        connectorDtoService.create(dto);
    }

     private void createJDBCConnector(String version) {
        CreateConnectorDto dto = CreateConnectorDto.builder()
                .name("JDBC_" + version).authorizationType(AuthorizationType.Basic)
                .typeId(findConnectorType("Foundation").getId()).namespace("jdbc").resource("applications")
                .version(version.toLowerCase()).properties(TEST_PROPERTIES).sourceUrl("https://localhost:8000/")
                .connectorUrl("https://localhost:8000/").sdkUrl("https://localhost:8000/")
                .imageUrl("https://localhost:8000/jdbc.png").description("test").build();

        connectorDtoService.create(dto);
    }

    private ConnectorType findConnectorType(String name) {
        return connectorTypeRepository.findAll()
                .stream()
                .filter(st -> st.getName().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("Connector type with passed name does not exist.")
                );
    }
}
