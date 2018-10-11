package io.electrica.user.rest;

import io.electrica.UserServiceApplication;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.service.UserDtoService;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
    UserRestClient userRestClient;


    @Test
    public void createUser() {
        ResponseEntity<UserDto> response = userRestClient.createUser(createUserDto());
        assertNotNull(response.getBody());
    }

    public CreateUserDto createUserDto() {
        CreateUserDto user = new CreateUserDto();
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(1L);
        organizationDto.setOrgName("test");
        organizationDto.setUuid(UUID.randomUUID());
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setUuid(UUID.randomUUID());
        user.setPassword("12345");
        user.setOrganization(organizationDto);
        return user;
    }


}
