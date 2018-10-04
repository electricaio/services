package io.electrica.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
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
            //builder.modules(new JavaTimeModule());
            builder.failOnUnknownProperties(false);
            if (!environmentType.isSafe()) {
                builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            }
        };
    }
}
