package io.electrica.migration;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.AuthorizationTypeRepository;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

import static io.electrica.connector.hub.model.enums.AuthorizationTypeName.*;

public class V0_0_1__01_default_connector_and_auth_types implements SpringJdbcMigration {

    private ConnectorTypeRepository connectorTypeRepository;

    private AuthorizationTypeRepository authorizationTypeRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();

        connectorTypeRepository = context.getBean(ConnectorTypeRepository.class);
        connectorTypeRepository.saveAll(
                Arrays.asList(
                        new ConnectorType("Foundation"),
                        new ConnectorType("CRM"),
                        new ConnectorType("Talent")
                )
        );

        authorizationTypeRepository = context.getBean(AuthorizationTypeRepository.class);
        authorizationTypeRepository.saveAll(
                Arrays.asList(
                        new AuthorizationType(BASIC_AUTHORIZATION),
                        new AuthorizationType(AWS_IAM_AUTHORIZATION),
                        new AuthorizationType(TOKEN_AUTHORIZATION))
        );
    }
}
