package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.AccessKey;
import io.electrica.user.repository.AccessKeyRepository;
import io.electrica.user.service.OrganizationDtoService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * UserRestClientTest to test rest client.
 */
public class AccessKeyClientTest extends UserServiceApplicationTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";
    private static final String TEST_ACCESS_KEY = "TestAccessKey";
    private static final String TEST_ACCESS_KEY2 = "TestAccessKey2";

    @Inject
    private OrganizationDtoService organizationDtoService;
    @Inject
    private AccessKeyRepository accessKeyRepository;

    @Inject
    private AccessKeyRestClient accessKeyRestClient;
    @Inject
    private UserRestClient userRestClient;

    private OrganizationDto defaultOrganization;

    @Before
    public void init() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        defaultOrganization = organizationDtoService.create(organizationDto);
    }

    @Test
    public void generateAccessKey() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AccessKeyDto result = accessKeyRestClient.generateAccessKey(accessKeyDto).getBody();

        assertCommonAccessKey(user, accessKeyDto, result, false);
        AccessKey accessKey = accessKeyRepository.findById(result.getId()).get();
        assertNotNull(accessKey.getAccessKey());
    }

    @Test
    public void findAllNonArchivedByUser() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto1 = createAccessKeyDto(user);
        AccessKeyDto accessKeyDto2 = createAccessKeyDto(user, TEST_ACCESS_KEY2);
        accessKeyRestClient.generateAccessKey(accessKeyDto1).getBody();
        accessKeyRestClient.generateAccessKey(accessKeyDto2).getBody();
        List<AccessKeyDto> resultList = accessKeyRestClient.findAllNonArchivedByUser(user.getId()).getBody();

        assertEquals(2, resultList.size());
        assertCommonAccessKey(user, accessKeyDto1, resultList.get(0), false);
        assertCommonAccessKey(user, accessKeyDto2, resultList.get(1), false, TEST_ACCESS_KEY2);
    }

    @Test
    public void getAccessKey() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AccessKeyDto generatedKey = accessKeyRestClient.generateAccessKey(accessKeyDto).getBody();
        AccessKeyDto result = accessKeyRestClient.getAccessKey(generatedKey.getId(), generatedKey.getUserId()).
                                                        getBody();

        assertCommonAccessKey(user, accessKeyDto, result, true);
    }

    private void assertCommonAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result,
                                        boolean keyPresent) {
        assertCommonAccessKey(user, accessKeyDto, result, keyPresent, TEST_ACCESS_KEY);
    }

    private void assertCommonAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result, 
                                        boolean keyPresent, String keyName) {
        assertNotSame(accessKeyDto, result);
        assertEquals(accessKeyDto.getKeyName(), keyName);
        assertEquals(accessKeyDto.getUserId(), user.getId());
        assertEquals(0L, (long) result.getRevisionVersion());
        if (keyPresent) {
            assertNotNull(result.getAccessKey());
        } else {
            assertNull(result.getAccessKey());
        }
    }

    private UserDto callCreateUser() {
        CreateUserDto createUserDto = createUserDto();
        ResponseEntity<UserDto> response = userRestClient.createUser(createUserDto);
        return response.getBody();
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

    private AccessKeyDto createAccessKeyDto(UserDto user) {
        return createAccessKeyDto(user, TEST_ACCESS_KEY);
    }

    private AccessKeyDto createAccessKeyDto(UserDto user, String keyName) {
        AccessKeyDto accessKeyDto = new AccessKeyDto();
        accessKeyDto.setKeyName(keyName);
        accessKeyDto.setUserId(user.getId());
        return accessKeyDto;
    }

}
