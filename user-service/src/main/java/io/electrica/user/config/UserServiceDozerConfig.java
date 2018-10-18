package io.electrica.user.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceDozerConfig {

    @Bean
    public BeanMappingBuilder userServiceMappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(UserDto.class, User.class)
                        .fields("id", "id")
                        .fields("organizationId", "organization.id");
                mapping(CreateUserDto.class, User.class)
                        .fields("id", "id")
                        .fields("password", "saltedPassword")
                        .fields("organizationId", "organization.id");
                mapping(AccessKeyDto.class, AccessKey.class)
                        .fields("id", "id")
                        .fields("userId", "user.id");
            }
        };
    }
}
