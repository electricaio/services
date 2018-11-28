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

    private static final String CONNECTOR_URL = "http://connector.dev.electrica.io";
    private static final String SDK_URL = "http://sdk.dev.electrica.io";

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
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("CRM").getId(),
                AuthorizationType.Token, "SalesForce " + version, "channel", version.toLowerCase(), "salesforce",
                "https://developer.salesforce.com/docs", CONNECTOR_URL, SDK_URL,
                "https://d1yjjnpx0p53s8.cloudfront.net/styles/logo-thumbnail/s3/102014/salesforce_logo.png", "test desciption",
                SLACK_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createSlackConnector(String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, "Slack Channel " + version, "channel", version.toLowerCase(), "slack",
                "https://api.slack.com/", CONNECTOR_URL, SDK_URL,
                "https://a.slack-edge.com/436da/marketing/img/meta/app-256.png", "test desciption",
                SLACK_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createJDBCConnector(String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.Basic, "JDBC " + version, "applications", version.toLowerCase(), "jdbc",
                "http://localhost", CONNECTOR_URL, SDK_URL,
                "http://d1.awsstatic.com/rdsImages/postgresql_logo.6de4615badd99412268bc6aa8fc958a0f403dd41.png",
                "test desciption", TEST_PROPERTIES);
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
