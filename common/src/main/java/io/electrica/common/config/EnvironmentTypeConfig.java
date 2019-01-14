package io.electrica.common.config;

import io.electrica.common.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Added to container {@link EnvironmentType} according to specified spring profile.
 */
@Configuration
public class EnvironmentTypeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentTypeConfig.class);

    private static final String PRODUCTION_ENV_PROFILE_NAME = "prod";
    private static final String DEVELOPMENT_ENV_PROFILE_NAME = "dev";
    private static final String STAGE_ENV_PROFILE_NAME = "stage";
    public static final String TEST_ENV_PROFILE_NAME = "test";

    @Bean
    @Profile(PRODUCTION_ENV_PROFILE_NAME)
    public EnvironmentType prodEnvironmentType() {
        LOGGER.info("Application started in Production mode");
        return EnvironmentType.Production;
    }

    @Bean
    @Profile(DEVELOPMENT_ENV_PROFILE_NAME)
    public EnvironmentType devEnvironmentType() {
        LOGGER.info("Application started in Development mode");
        return EnvironmentType.Development;
    }

    @Bean
    @Profile(TEST_ENV_PROFILE_NAME)
    public EnvironmentType testEnvironmentType() {
        LOGGER.info("Application started in Test mode");
        return EnvironmentType.Test;
    }

    @Bean
    @Profile(STAGE_ENV_PROFILE_NAME)
    public EnvironmentType stageEnvironmentType() {
        LOGGER.info("Application started in Stage mode");
        return EnvironmentType.Stage;
    }

    @Bean
    @ConditionalOnMissingBean
    public EnvironmentType defaultEnvironmentType() {
        LOGGER.info("Application started in Default mode");
        return EnvironmentType.Default;
    }
}
