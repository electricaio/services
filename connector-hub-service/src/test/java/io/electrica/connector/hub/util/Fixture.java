package io.electrica.connector.hub.util;

import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AuthorizationTypeRepository;
import io.electrica.connector.hub.repository.ConnectionRepository;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;
import io.electrica.connector.hub.rest.dto.CreateConnectorDto;

public interface Fixture {

    default CreateConnectorDto createHackerRankConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("HackerRank");
        dto.setResource("Applications");
        dto.setNamespace("com.hackerrank");
        dto.setTypeId(
                findConnectorType("Talent").getId()
        );
        dto.setAuthorizationTypeId(
                findAuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION).getId()
        );
        dto.setVersion("1.0");
        return dto;
    }

    default CreateConnectorDto createGreenhouseConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("Greenhouse");
        dto.setNamespace("com.greenhouse");
        dto.setTypeId(
                findConnectorType("Talent").getId()
        );
        dto.setAuthorizationTypeId(
                findAuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION).getId()
        );
        dto.setVersion("1.1");
        return dto;
    }

    default CreateConnectorDto createMySQLConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("MySQL");
        dto.setNamespace("com.mysql");
        dto.setTypeId(
                findConnectorType("Foundation").getId()
        );
        dto.setAuthorizationTypeId(
                findAuthorizationType(AuthorizationTypeName.BASIC_AUTHORIZATION).getId()
        );
        dto.setVersion("5.6");
        return dto;
    }

    default AuthorizationType findAuthorizationType(AuthorizationTypeName name) {
        return getAuthorizationTypeRepository().findAll()
                .stream()
                .filter(at -> at.getName().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("Authorization type with passed name does not exist.")
                );
    }

    default ConnectorType findConnectorType(String name) {
        return getConnectorTypeRepository().findAll()
                .stream()
                .filter(st -> st.getName().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("Connector type with passed name does not exist.")
                );
    }

    ConnectionRepository getConnectionRepository();

    ConnectorTypeRepository getConnectorTypeRepository();

    AuthorizationTypeRepository getAuthorizationTypeRepository();
}
