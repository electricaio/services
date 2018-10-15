package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.OrganizationDtoService;
import io.electrica.user.service.UserService;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * UserRestClientTest to test rest client.
 */
@NoArgsConstructor
public class UserRestClientTest extends UserServiceApplicationTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";

    @Inject
    OrganizationDtoService organizationDtoService;
    @Inject
    UserRestClient userRestClient;
    @Inject
    UserService userService;
    @Inject
    PasswordEncoder passwordEncoder;

    private OrganizationDto defaultOrganization;

    @Before
    public void init() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setIsActive(Boolean.TRUE);
        organizationDto.setUuid(UUID.randomUUID());
        defaultOrganization = organizationDtoService.create(organizationDto);
    }

    @Test
    public void createUserTest() {
        CreateUserDto createUserDto = createUserDto();
        ResponseEntity<UserDto> response = userRestClient.createUser(createUserDto);
        UserDto result = response.getBody();
        assertNotNull(result.getId());
        assertEquals(result.getEmail(), createUserDto.getEmail());
        assertEquals(result.getFirstName(), createUserDto.getFirstName());
        assertEquals(result.getLastName(), createUserDto.getLastName());
        assertNotNull(result.getRevisionVersion());
    }

    @Test
    public void saltedPasswordTest() {
        CreateUserDto createUserDto = createUserDto();
        ResponseEntity<UserDto> response = userRestClient.createUser(createUserDto);
        UserDto result = response.getBody();
        User saltedUSer = userService.findById(result.getId(), false);
        Assert.assertNotEquals(saltedUSer.getSaltedPassword(), createUserDto.getPassword());
        assertTrue(passwordEncoder.matches(createUserDto.getPassword(), saltedUSer.getSaltedPassword()));

    }

    public CreateUserDto createUserDto() {
        long random = new Random().nextInt(10000);
        CreateUserDto user = new CreateUserDto();
        user.setEmail(DEFAULT_EMAIL + random);
        user.setFirstName("FirstName" + random);
        user.setLastName("LastName" + random);
        user.setUuid(UUID.randomUUID());
        user.setPassword("12345");
        user.setOrganizationId(defaultOrganization.getId());
        return user;
    }
}
