package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserRestClientImplTest {

    private static final String DEFAULT_EMAIL="test@localhost.com";

    UserService userService;

    UserRestClient userRestClient;

    @Before
    public void setup() {
        userService =mock(UserService.class);
        userRestClient = new UserRestClientImpl(userService);
    }

    @Test
    public void createUserTest() throws URISyntaxException{
        CreateUserDto createUserDto = createEntityCreateUserDto();
        User user = createEntityUser();
        when(userService.findOneByLogin(DEFAULT_EMAIL)).thenReturn(Optional.empty());
        when(userService.createUser(createUserDto)).thenReturn(user);
        User actualUser = userRestClient.createUser(createUserDto).getBody();
        assertEquals(user,actualUser);
    }

    @Test
    public void createUserTestWithLoginAlreadyExists() throws URISyntaxException{
        CreateUserDto createUserDto = createEntityCreateUserDto();
        when(userService.findOneByLogin(DEFAULT_EMAIL)).thenReturn(Optional.of(createEntityUser()));
        assertEquals(userRestClient.createUser(createUserDto).getStatusCode(), HttpStatus.CONFLICT);

    }

    public static CreateUserDto createEntityCreateUserDto() {
        CreateUserDto user = new CreateUserDto();
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName("FirstName");
        return user;
    }

    public static User createEntityUser() {
        User user = new User();
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName("FirstName");
        return user;
    }



}
