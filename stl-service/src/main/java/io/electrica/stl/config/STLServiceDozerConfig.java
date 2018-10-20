package io.electrica.stl.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.stl.model.STL;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class STLServiceDozerConfig {

    @Bean
    public BeanMappingBuilder mappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CreateSTLDto.class, STL.class)
                        .fields("typeId", "type.id");
                mapping(ReadSTLDto.class, STL.class)
                        .fields("typeId", "type.id");
            }
        };
    }
}
