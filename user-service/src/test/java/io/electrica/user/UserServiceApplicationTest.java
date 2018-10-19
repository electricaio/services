package io.electrica.user;

import io.electrica.UserServiceApplication;
import io.electrica.test.AbstractJpaApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.rest.OrganizationController;
import io.electrica.user.rest.UserController;
import io.electrica.user.service.OrganizationDtoService;
import io.electrica.user.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = UserServiceApplication.class)
public abstract class UserServiceApplicationTest extends AbstractJpaApplicationTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";

    protected OrganizationDto defaultOrganization;

    @Inject
    protected OrganizationDtoService organizationDtoService;

    @Inject
    protected UserController userController;

    @Inject
    protected UserService userService;

    @Inject
    protected PasswordEncoder passwordEncoder;

    @Inject
    protected OrganizationController organizationController;


    protected void initBaseClass() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        defaultOrganization = organizationDtoService.create(organizationDto);
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

    public CreateUserDto createUserDtoWithoutOrg() {
        CreateUserDto user = createUserDto();
        user.setOrganizationId(null);
        return user;
    }

    public UserDto createAndSaveUser() {
        return userController.createUser(createUserDto()).getBody();
    }

    public OrganizationDto createAndSaveNewOrganization() {

        return organizationController.createIfAbsent(createNewOrganization()).getBody();
    }

    public OrganizationDto createNewOrganization() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        return organizationDto;
    }

    public void equals(UserDto u1, UserDto u2) {
        assertEquals(u1.getId(), u2.getId());
        assertEquals(u1.getEmail(), u2.getEmail());
        assertEquals(u1.getRevisionVersion(), u2.getRevisionVersion());
        assertEquals(u1.getFirstName(), u2.getFirstName());
        assertEquals(u1.getLastName(), u2.getLastName());
        assertEquals(u1.getUuid(), u2.getUuid());
        assertEquals(u1.getOrganizationId(), u2.getOrganizationId());
    }
}
