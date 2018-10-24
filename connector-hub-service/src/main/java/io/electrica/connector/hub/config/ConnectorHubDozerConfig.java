package io.electrica.connector.hub.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.CreateConnectorDto;
import io.electrica.connector.hub.rest.dto.ReadConnectorDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorHubDozerConfig {

    @Bean
    public BeanMappingBuilder mappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CreateConnectorDto.class, Connector.class)
                        .fields("typeId", "type.id")
                        .fields("authorizationTypeId", "authorizationType.id");
                mapping(ReadConnectorDto.class, Connector.class)
                        .fields("typeId", "type.id")
                        .fields("authorizationTypeId", "authorizationType.id");
                mapping(ConnectDto.class, Connection.class)
                        .fields("connectorId", "connector.id");
            }
        };
    }
}
