package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.UserDtoService;
import org.junit.Before;

import static org.mockito.Mockito.mock;

/**
 * REST Client implementation for Managing users..
 */
public class UserRestClientImplTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";

    UserDtoService userDtoService;

    UserRestClient userRestClient;

    @Before
    public void setup() {
        userDtoService = mock(UserDtoService.class);
        userRestClient = new UserRestClientImpl(userDtoService);
    }

    /*@Test
    public void createUserTest() {
        CreateUserDto createUserDto = createEntityCreateUserDto();
        User user = createEntityUser();
        when(userDtoService.findOneByLogin(DEFAULT_EMAIL)).thenReturn(Optional.empty());
        when(userService.createUser(createUserDto)).thenReturn(user);
        UserDto actualUser = userRestClient.createUser(createUserDto).getBody();
        assertEquals(user,actualUser);
    }

    @Test
    public void createUserTestWithLoginAlreadyExists() throws URISyntaxException{
        CreateUserDto createUserDto = createEntityCreateUserDto();
        when(userService.findOneByLogin(DEFAULT_EMAIL)).thenReturn(Optional.of(createEntityUser()));
        assertEquals(userRestClient.createUser(createUserDto).getStatusCode(), HttpStatus.CONFLICT);

    }*/

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
