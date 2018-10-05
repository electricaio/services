package io.electrica.user.rest;

import io.electrica.UserServiceApplication;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 *  UserRestClientTest to test rest client.
 *
 */

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
        ResponseEntity<UserDto> response = userRestClient.createUser(new CreateUserDto());
        assertNotNull(response.getBody());

    }

}
