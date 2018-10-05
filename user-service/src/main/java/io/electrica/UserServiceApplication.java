package io.electrica;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main class to start the User Service.
 *
 * Please see the {@link io.electrica.UserServiceApplication} class for true identity
 * @author Munish Sodhi
 *
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
