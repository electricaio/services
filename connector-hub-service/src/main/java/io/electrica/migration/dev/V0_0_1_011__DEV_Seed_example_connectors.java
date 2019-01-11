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

public class V0_0_1_011__DEV_Seed_example_connectors implements SpringJdbcMigration {

    private static final String SDK_URL = "https://www.electrica.io";
    private static final String SOURCE_URL = "https://www.electrica.io";
    private static final Map<String, String> TEST_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

    private ConnectorDtoService connectorDtoService;
    private ConnectorTypeRepository connectorTypeRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        final ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();
        connectorDtoService = context.getBean(ConnectorDtoService.class);
        connectorTypeRepository = context.getBean(ConnectorTypeRepository.class);

        createGreenhouseApplicationConnector("Greenhouse Applications", "1");
        createSCIMConnector("SCIM", "1");
        createSmartRecruitersApplicationsConnector("SmartRecruiters Applications", "1");
        createLeverApplicationsConnector("Lever Applications", "1");
        createIncomingWebhooksConnector("Incoming Webhooks", "1");
        createMySQLConnector("MySQL", "1");
    }

    private void createGreenhouseApplicationConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, name, "Greenhouse Applications Connector", "greenhouse", "applications", version.toLowerCase(),
                SOURCE_URL, "https://www.greenhouse.io", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/greenhouse-logo.png",
                TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createSCIMConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.Token, name, "SCIM Connector", "scim", "user", version.toLowerCase(),
                SOURCE_URL, "https://tools.ietf.org/html/rfc7644", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/scim-logo.png",
                TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createSmartRecruitersApplicationsConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, name, "SmartRecruiters Applications Connector", "smartrecruiters", "applications", version.toLowerCase(),
                SOURCE_URL, "https://www.smartrecruiters.com", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/smartrecruiters-logo.png",
                TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createLeverApplicationsConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Talent").getId(),
                AuthorizationType.Token, name, "Lever Applications Connector", "lever", "applications", version.toLowerCase(),
                SOURCE_URL, "https://www.lever.co", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/lever-logo.png",
                TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createIncomingWebhooksConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.None, name, "Incoming Webhooks Connector", "webhooks", "incoming", version.toLowerCase(),
                SOURCE_URL, "https://www.electrica.io", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/webhooks-logo.png",
                TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private void createMySQLConnector(String name, String version) {
        CreateConnectorDto dto = new CreateConnectorDto(findConnectorType("Foundation").getId(),
                AuthorizationType.Basic, name, "MySQL Connector", "mysql", "db", version.toLowerCase(),
                SOURCE_URL, "https://www.mysql.com", SDK_URL,
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/mysql-logo.png",
                TEST_PROPERTIES);
        connectorDtoService.create(dto);
    }

    private ConnectorType findConnectorType(String name) {
        return connectorTypeRepository.findAll()
                .stream()
                .filter(st -> st.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Connector type with passed name does not exist."));
    }
}
