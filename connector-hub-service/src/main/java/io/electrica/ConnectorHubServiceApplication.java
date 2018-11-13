package io.electrica;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to start the Connector Hub Service.
 */

@SpringBootApplication
public class ConnectorHubServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(ConnectorHubServiceApplication.class, args);
    }
}
