package io.electrica.user.rest;

import io.electrica.UserServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 *  UserRestClientTest to test rest client.
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = UserServiceApplication.class,
        properties = "",
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class UserRestClientTest {


//    private UserRestClient userRestClient;
//    private UserService userService;

//    public UserRestClientTest(UserService userService) {
//        this.userService = userService;
//    }

    @Test
    public void createUser()  {
        assertTrue(true);
        // TODO: Once we design all layers, we will update the unit tests
//        ResponseEntity<User> response = userRestClient.createUser(new CreateUserDto());
//        assertNotNull(response.getBody());
    }


}
