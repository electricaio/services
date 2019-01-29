package io.electrica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Main class to start the Metric Service.
 */

@EnableRetry
@SpringBootApplication
public class MetricServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(MetricServiceApplication.class, args);
    }
}
