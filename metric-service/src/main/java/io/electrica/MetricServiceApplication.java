package io.electrica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to start the Metric Service.
 */

@SpringBootApplication
public class MetricServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(MetricServiceApplication.class, args);
    }
}
