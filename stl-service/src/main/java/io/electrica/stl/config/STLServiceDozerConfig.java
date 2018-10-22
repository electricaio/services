package io.electrica.stl.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.stl.model.Connector;
import io.electrica.stl.rest.dto.CreateConnectorDto;
import io.electrica.stl.rest.dto.ReadConnectorDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class STLServiceDozerConfig {

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
            }
        };
    }
}
