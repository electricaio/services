package io.electrica.webhook.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.model.Webhook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

import static com.github.dozermapper.core.loader.api.FieldsMappingOptions.copyByReference;
import static com.github.dozermapper.core.loader.api.FieldsMappingOptions.hintA;

@Configuration
public class WebhookDozerConfig {

    @Bean
    public BeanMappingBuilder mappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(ConnectionCreateWebhookDto.class, Webhook.class)
                        .fields("properties", "properties", copyByReference(), hintA(HashMap.class));
            }
        };
    }
}
