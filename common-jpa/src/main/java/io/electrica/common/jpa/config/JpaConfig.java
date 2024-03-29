package io.electrica.common.jpa.config;

import io.electrica.common.config.CommonModuleConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Instant;
import java.util.Optional;

@Configuration
@Import(CommonModuleConfig.class)
@PropertySource("classpath:/common-jpa.properties")

@EntityScan("io.electrica")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "io.electrica")
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class JpaConfig {

    @Bean
    public static DateTimeProvider dateTimeProvider() {
        //depends on time zone config, so can use default
        return () -> Optional.of(Instant.now());
    }
}
