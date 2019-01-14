package io.electrica.migration;

import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.ConnectorTypeRepository;
import io.electrica.connector.hub.service.dto.ConnectorDtoService;
import org.springframework.beans.factory.BeanFactory;

import java.util.Objects;

public class MigrationUtils {

    private MigrationUtils() {
    }

    public static ConnectorDto saveConnector(BeanFactory beanFactory, CreateConnectorDto connector, String typeName) {
        ConnectorDtoService connectorDtoService = beanFactory.getBean(ConnectorDtoService.class);
        ConnectorType connectorType = findConnectorType(beanFactory, typeName);
        connector.setTypeId(connectorType.getId());
        return connectorDtoService.create(connector);
    }

    public static ConnectorType findConnectorType(BeanFactory beanFactory, String name) {
        ConnectorTypeRepository connectorTypeRepository = beanFactory.getBean(ConnectorTypeRepository.class);
        return connectorTypeRepository.findAll()
                .stream()
                .filter(st -> Objects.equals(st.getName(), name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Connector type not found by name: " + name));
    }
}
