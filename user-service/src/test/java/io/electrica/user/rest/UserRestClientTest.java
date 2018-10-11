package io.electrica.user.rest;

import io.electrica.UserServiceApplication;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.service.OrganizationDtoService;
import io.electrica.user.service.UserDtoService;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * UserRestClientTest to test rest client.
 */

@NoArgsConstructor
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = UserServiceApplication.class,
        properties = "",
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class UserRestClientTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";


    @Autowired
    UserDtoService userDtoService;
    @Autowired
    OrganizationDtoService organizationDtoService;
    @Autowired
    UserRestClient userRestClient;

    private OrganizationDto defaultOrganization;

    @Before
    public void init() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        defaultOrganization = organizationDtoService.create(organizationDto);
    }

    @Test
    @Transactional
    public void createUser() {
        ResponseEntity<UserDto> response = userRestClient.createUser(createUserDto());
        assertNotNull(response.getBody());
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
