package io.electrica.webhook.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import java.util.UUID;

@Slf4j
@Configuration
public class WebhookConfig {

    public static final String INSTANCE_ID_QUALIFIER = "serviceInstanceId";

    @Bean
    @Named(INSTANCE_ID_QUALIFIER)
    public UUID serviceInstanceId() {
        UUID uuid = UUID.randomUUID();
        log.info("Service instance identifier: " + uuid);
        return uuid;
    }
}
