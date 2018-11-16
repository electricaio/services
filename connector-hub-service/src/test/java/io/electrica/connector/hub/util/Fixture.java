package io.electrica.connector.hub.util;

import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;

import java.util.HashMap;
import java.util.Map;

public interface Fixture {

    Map<String, String> TEST_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

    default CreateConnectorDto createHackerRankConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("HackerRank");
        dto.setResource("Applications");
        dto.setNamespace("com.hackerrank");
        dto.setTypeId(
                findConnectorType("Talent").getId()
        );
        dto.setAuthorizationType(AuthorizationType.Token);
        dto.setVersion("1.0");
        dto.setProperties(TEST_PROPERTIES);
        return dto;
    }

    default CreateConnectorDto createGreenhouseConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("Greenhouse");
        dto.setNamespace("com.greenhouse");
        dto.setTypeId(
                findConnectorType("Talent").getId()
        );
        dto.setAuthorizationType(AuthorizationType.Token);
        dto.setVersion("1.1");
        dto.setProperties(TEST_PROPERTIES);
        return dto;
    }

    default CreateConnectorDto createMySQLConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("MySQL");
        dto.setNamespace("com.mysql");
        dto.setTypeId(
                findConnectorType("Foundation").getId()
        );
        dto.setAuthorizationType(AuthorizationType.Basic);
        dto.setVersion("5.6");
        dto.setProperties(TEST_PROPERTIES);
        return dto;
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

    ConnectorTypeRepository getConnectorTypeRepository();

}
