package io.electrica.connector.hub.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.Connector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

import static com.github.dozermapper.core.loader.api.FieldsMappingOptions.copyByReference;
import static com.github.dozermapper.core.loader.api.FieldsMappingOptions.hintA;

@Configuration
public class ConnectorHubDozerConfig {

    @Bean
    public BeanMappingBuilder mappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CreateConnectorDto.class, Connector.class)
                        .fields("typeId", "type.id")
                        .fields("properties", "properties", copyByReference(), hintA(HashMap.class));

                mapping(ConnectorDto.class, Connector.class)
                        .fields("typeId", "type.id");

                mapping(CreateConnectionDto.class, Connection.class)
                        .fields("connectorId", "connector.id")
                        .fields("properties", "properties")
                        .fields("properties", "properties",
                                copyByReference(), hintA(HashMap.class));

                mapping(ConnectionDto.class, Connection.class)
                        .fields("connectorId", "connector.id")
                        .fields("authorizationId", "authorization.id");
            }
        };
    }
}
