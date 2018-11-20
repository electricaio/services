package io.electrica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to start the Webhook Service.
 */

@SpringBootApplication
public class WebhookServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(WebhookServiceApplication.class, args);
    }
}
