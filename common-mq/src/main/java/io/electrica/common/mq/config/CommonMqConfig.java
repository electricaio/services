package io.electrica.common.mq.config;

import io.electrica.common.config.CommonModuleConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(CommonModuleConfig.class)
@PropertySource("classpath:/common-mq.properties")
public class CommonMqConfig {
}
