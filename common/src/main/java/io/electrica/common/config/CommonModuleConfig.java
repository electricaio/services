package io.electrica.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.TimeZone;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:/common.properties")
@PropertySource("classpath:/assemble.properties")
public class CommonModuleConfig {

    static {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(utcTimeZone);
    }

}
