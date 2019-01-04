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

    private static final String SDK_URL = "https://www.electrica.io";
    private static final String SOURCE_URL = "https://www.electrica.io";

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

    Map<String, String> ECHO_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "https://www.electrica.io");
    }};


    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();
        connectorDtoService = context.getBean(ConnectorDtoService.class);
        connectorTypeRepository = context.getBean(ConnectorTypeRepository.class);

        createSlackConnector("Slack Channel V1", "1");
        createEchoConnector("Echo Test V1", "1");
        createGreenhouseApplicationConnector("Greenhouse Applications", "1");
        createSCIMConnector("SCIM", "1");
        createSmartRecruitersApplicationsConnector("SmartRecruiters Applications", "1");
        createLeverApplicationsConnector("Lever Applications", "1");
        createIncomingWebhooksConnector("Incoming Webhooks", "1");
        createMySQLConnector("MySQL", "1");
    }

    private void createSlackConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.Token, name, "channel", version.toLowerCase(), "slack",
                SOURCE_URL, "https://www.slack.com", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/slack-logo.png", "Slack Channel " + version + " Connector",
                SLACK_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createEchoConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.None, name, "test", version.toLowerCase(),
                "echo", SOURCE_URL, "https://www.electrica.io", SDK_URL,
                "https://d1yjjnpx0p53s8.cloudfront.net/styles/logo-original-577x577/s3/0022/0974/brand.gif",
                "Echo Test Connector", ECHO_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createGreenhouseApplicationConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, name, "applications", version.toLowerCase(), "greenhouse",
                SOURCE_URL, "https://www.greenhouse.io", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/greenhouse-logo.png",
                "Greenhouse Applications Connector", TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createSCIMConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.Token, name, "user", version.toLowerCase(), "scim",
                SOURCE_URL, "https://tools.ietf.org/html/rfc7644", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/scim-logo.png",
                "SCIM Connector", TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createSmartRecruitersApplicationsConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, name, "applications", version.toLowerCase(), "smartrecruiters",
                SOURCE_URL, "https://www.smartrecruiters.com", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/smartrecruiters-logo.png",
                "SmartRecruiters Applications Connector", TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createLeverApplicationsConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, name, "applications", version.toLowerCase(), "lever",
                SOURCE_URL, "https://www.lever.co", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/lever-logo.png",
                "Lever Applications Connector", TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createIncomingWebhooksConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.None, name, "incoming", version.toLowerCase(), "webhooks",
                SOURCE_URL, "https://www.electrica.io", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/webhooks-logo.png",
                "Incoming Webhooks Connector", TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createMySQLConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.Basic, name, "db", version.toLowerCase(), "mysql",
                SOURCE_URL, "https://www.mysql.com", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/mysql-logo.png",
                "MySQL Connector", TEST_PROPERTIES);
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
