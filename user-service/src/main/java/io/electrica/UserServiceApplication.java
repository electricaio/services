package io.electrica;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class to start the User Service.
 * <p>
 * Please see the {@link io.electrica.UserServiceApplication} class for true identity
 */
@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
