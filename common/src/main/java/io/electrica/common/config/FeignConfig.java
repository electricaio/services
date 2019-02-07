package io.electrica.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@Configuration
@EnableFeignClients(basePackages = {"io.electrica"})
public class FeignConfig {

    @Bean
    public FeignFormatterRegistrar localDateFeignFormatterRegistrar() {
        return new FeignFormatterRegistrar() {
            @Override
            public void registerFormatters(FormatterRegistry formatterRegistry) {
                DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
                registrar.setUseIsoFormat(true);
                registrar.registerFormatters(formatterRegistry);
            }
        };
    }

}
