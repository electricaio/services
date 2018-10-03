package io.electrica.user.rest;

import io.electrica.UserServiceApplication;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = UserServiceApplication.class,
        properties = ""
)
public class UserRestClientTest {

    @Inject
    private UserRestClient userRestClient;

    @Test
    public void createUser() {
        ResponseEntity<UserDto> user = userRestClient.createUser(new CreateUserDto());
        Assert.notNull(user.getBody());
    }

}
