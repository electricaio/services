package io.electrica.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.electrica.common.EnvironmentType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public static Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(
            EnvironmentType environmentType
    ) {
        return builder -> {
            // skip unknown properties
            builder.failOnUnknownProperties(false);
            builder.modules(new JavaTimeModule());

            // include nullable properties for safe environment to simplify development and testing
            if (!environmentType.isSafe()) {
                builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            }
        };
    }
}
