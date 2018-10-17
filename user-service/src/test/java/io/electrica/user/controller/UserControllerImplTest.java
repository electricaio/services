package io.electrica.user.controller;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.rest.UserController;
import io.electrica.user.rest.UserControllerImpl;
import io.electrica.user.service.UserDtoService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * REST Client implementation test.
 */
public class UserControllerImplTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";

    private UserDtoService userDtoService;

    UserController userController;

    @Before
    public void setup() {
        userDtoService = mock(UserDtoService.class);
        userController = new UserControllerImpl(userDtoService);
    }

    @Test
    public void createUserTest() {
        CreateUserDto createUserDto = createEntityCreateUserDto();
        when(userDtoService.create(createUserDto)).thenReturn(createUserDto);
        UserDto actualUser = userController.createUser(createUserDto).getBody();
        assertEquals(createUserDto, actualUser);
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
