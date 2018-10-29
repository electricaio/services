package io.electrica;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ConnectorHubServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(ConnectorHubServiceApplication.class, args);
    }
}
