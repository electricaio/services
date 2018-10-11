package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

public class UserRestClientTest extends UserServiceApplicationTest {

    @Inject
    private UserRestClient userRestClient;

    @Test
    public void createUser() {
        ResponseEntity<UserDto> response = userRestClient.createUser(new CreateUserDto());
        assertNotNull(response.getBody());
    }

}
