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

import java.util.HashMap;
import java.util.Map;

public class V0_0_1_030__Seed_Slack_Channel_v1_connector implements SpringJdbcMigration {

    private static final String CONNECTOR_TYPE = "Foundation";
    private static final Map<String, String> PROPERTIES = new HashMap<String, String>() {{
        put("send-message.url-template", "https://hooks.slack.com/services/%s");
        put("http-client.max-idle-connections", "10");
        put("http-client.keep-alive-duration-min", "60");
    }};

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
                AuthorizationType.Token,
                "Slack Channel v1",
                "Connector allow you send messages to some channel in Slack",
                "slack",
                "channel",
                "1",
                "https://www.electrica.io",
                "https://www.slack.com",
                "https://www.electrica.io",
                "https://s3.us-east-2.amazonaws.com/images.electrica.io/slack-logo.png",
                PROPERTIES
        );
        connectorDtoService.create(connector);
    }
}
