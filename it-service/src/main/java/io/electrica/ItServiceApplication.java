package io.electrica;

import io.electrica.common.interceptor.FeignRequestInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"io.electrica"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = FeignRequestInterceptor.class))
public class ItServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(ItServiceApplication.class, args);
    }

}
