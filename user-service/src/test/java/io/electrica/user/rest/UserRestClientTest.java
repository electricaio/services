package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

/**
 * UserRestClientTest to test rest client.
 */
@NoArgsConstructor
public class UserRestClientTest extends UserServiceApplicationTest {


    private static final String TEST_ACCESS_KEY = "TestAccessKey";


    @Before
    public void init() {
        initBaseClass();
    }

    @Test
    public void createUserTest() {
        CreateUserDto createUserDto = createUserDto();
        UserDto result = callCreateUser(createUserDto);
        assertNotNull(result.getId());
        assertEquals(result.getEmail(), createUserDto.getEmail());
        assertEquals(result.getFirstName(), createUserDto.getFirstName());
        assertEquals(result.getLastName(), createUserDto.getLastName());
        assertNotNull(result.getRevisionVersion());
    }

    @Test
    public void saltedPasswordTest() {
        CreateUserDto createUserDto = createUserDto();
        UserDto result = callCreateUser(createUserDto);
        User saltedUSer = userService.findById(result.getId(), false);
        Assert.assertNotEquals(saltedUSer.getSaltedPassword(), createUserDto.getPassword());
        assertTrue(passwordEncoder.matches(createUserDto.getPassword(), saltedUSer.getSaltedPassword()));
    }

    @Test
    public void generateAccessKey() {
        CreateUserDto createUserDto = createUserDto();
        UserDto user = callCreateUser(createUserDto);
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AccessKeyDto result = userRestClient.generateAccessKey(accessKeyDto).getBody();

        assertNotSame(accessKeyDto, result);
        assertEquals(accessKeyDto.getKeyName(), TEST_ACCESS_KEY);
        assertEquals(accessKeyDto.getUserId(), user.getId());
        assertEquals(0L, (long) result.getRevisionVersion());
        assertNull(result.getAccessKey());

        AccessKey accessKey = accessKeyRepository.findById(result.getId()).get();
        assertNotNull(accessKey.getAccessKey());
    }

    private UserDto callCreateUser(CreateUserDto createUserDto) {
        ResponseEntity<UserDto> response = userRestClient.createUser(createUserDto);
        return response.getBody();
    }


    private AccessKeyDto createAccessKeyDto(UserDto user) {
        AccessKeyDto accessKeyDto = new AccessKeyDto();
        accessKeyDto.setKeyName(TEST_ACCESS_KEY);
        accessKeyDto.setUserId(user.getId());
        return accessKeyDto;
    }

}
