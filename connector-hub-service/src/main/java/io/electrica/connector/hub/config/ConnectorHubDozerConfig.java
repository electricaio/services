package io.electrica.connector.hub.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.dto.ConnectorDto;
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
                        .fields("typeId", "type.id");

                mapping(ConnectorDto.class, Connector.class)
                        .fields("typeId", "type.id");

                mapping(CreateConnectionDto.class, Connection.class)
                        .fields("connectorId", "connector.id");

                mapping(ConnectionDto.class, Connection.class)
                        .fields("connectorId", "connector.id")
                        .fields("authorizationId", "authorization.id");
            }
        };
    }
}
