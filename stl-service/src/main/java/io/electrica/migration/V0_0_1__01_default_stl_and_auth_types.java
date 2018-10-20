package io.electrica.migration;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.AuthorizationTypeRepository;
import io.electrica.stl.repository.STLTypeRepository;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

import static io.electrica.stl.model.enums.AuthorizationTypeName.*;

public class V0_0_1__01_default_stl_and_auth_types implements SpringJdbcMigration {

    private STLTypeRepository stlTypeRepository;

    private AuthorizationTypeRepository authorizationTypeRepository;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final ApplicationContext context = FlywayApplicationContextBridge.getApplicationContext();

        stlTypeRepository = context.getBean(STLTypeRepository.class);
        stlTypeRepository.saveAll(
                Arrays.asList(
                        new STLType("Foundation"),
                        new STLType("CRM"),
                        new STLType("Talent")
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
