package io.electrica.user;

import io.electrica.UserServiceApplication;
import io.electrica.test.AbstractJpaApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.repository.AccessKeyRepository;
import io.electrica.user.repository.OrganizationRepository;
import io.electrica.user.rest.AccessKeyController;
import io.electrica.user.rest.OrganizationController;
import io.electrica.user.rest.UserController;
import io.electrica.user.service.OrganizationDtoService;
import io.electrica.user.service.OrganizationService;
import io.electrica.user.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(classes = UserServiceApplication.class)
public abstract class UserServiceApplicationTest extends AbstractJpaApplicationTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";

    protected OrganizationDto defaultOrganization;

    @Inject
    protected OrganizationDtoService organizationDtoService;


    @Inject
    protected AccessKeyController accessKeyController;

    @Inject
    protected AccessKeyRepository accessKeyRepository;

    @Inject
    protected UserController userController;

    @Inject
    protected UserService userService;

    @Inject
    protected PasswordEncoder passwordEncoder;

    @Inject
    protected OrganizationController organizationController;

    @Inject
    protected OrganizationRepository organizationRepository;

    @Inject
    protected OrganizationService organizationService;


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

        return organizationController.create(createNewOrganization()).getBody();
    }

    public OrganizationDto createNewOrganization() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        return organizationDto;
    }
}
