package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for user controller.
 */
@NoArgsConstructor
public class UserControllerTest extends UserServiceApplicationTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";

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
    public void getUsersForOrganizationTest() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        List<UserDto> userDtos = userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
        assertEquals(2, userDtos.size());
        assertEquals(u1.getId(), userDtos.get(0).getId());
        assertEquals(u2.getId(), userDtos.get(1).getId());
    }

    @Test
    public void getUsersForOrganizationWithMultipleOrgsTest() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        OrganizationDto organizationDto = createAndSaveNewOrganization();
        UserDto u3 = createUserDto();
        u3.setOrganizationId(organizationDto.getId());
        List<UserDto> userDtos = userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
        assertEquals(2, userDtos.size());
        assertEquals(u1.getId(), userDtos.get(0).getId());
        assertEquals(u2.getId(), userDtos.get(1).getId());
    }

    @Test
    public void getUsersForOrganizationWithOrgNotExist() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        List<UserDto> userDtos = userController.getUsersForOrganization(121212L).getBody();
        assertEquals(0, userDtos.size());
    }

    @Test
    public void getUsersForOrganizationWithNoUsers() {
        List<UserDto> userDtos = userController.getUsersForOrganization(defaultOrganization.getId()).getBody();
        assertEquals(0, userDtos.size());
    }

    private UserDto callCreateUser(CreateUserDto createUserDto) {
        ResponseEntity<UserDto> response = userController.createUser(createUserDto);
        return response.getBody();
    }

}
