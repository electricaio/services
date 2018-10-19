package io.electrica.user.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForUser;
import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for access key controller.
 */
public class AccessKeyControllerTest extends UserServiceApplicationTest {

    private static final String DEFAULT_EMAIL = "test@localhost.com";
    private static final String TEST_ACCESS_KEY = "TestAccessKey";
    private static final String TEST_ACCESS_KEY2 = "TestAccessKey2";

    @Inject
    private OrganizationController organizationRestClient;
    @Inject
    private AccessKeyController accessKeyController;
    @Inject
    private UserController userRestClient;

    private OrganizationDto defaultOrganization;

    @Before
    public void init() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        defaultOrganization = organizationRestClient.createIfAbsent(organizationDto).getBody();
    }

    /**
     * Generate access key success flow.
     */
    @Test
    public void generateAccessKey() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey), 
                () -> {
                    AccessKeyDto result = accessKeyController.generateAccessKey(accessKeyDto).getBody();

                    assertCommonAccessKey(user, accessKeyDto, result, false);
                });
    }

    /**
     * Not for self user.
     */
    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.CreateAccessKey)
    public void generateAccessKeyNoAccessIncorrectUser() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        accessKeyController.generateAccessKey(accessKeyDto).getBody();
    }

    /**
     * No Permission for key generation.
     */
    @Test(expected = AccessDeniedException.class)
    public void generateAccessKeyNoPermission() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadOrg), 
                () -> {
                    accessKeyController.generateAccessKey(accessKeyDto).getBody();
                });
    }

    /**
     * Getting list of key success flow.
     */
    @Test
    public void findAllNonArchivedByUser() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto1 = createAccessKeyDto(user);
        AccessKeyDto accessKeyDto2 = createAccessKeyDto(user, TEST_ACCESS_KEY2);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey), 
                () -> {
                    accessKeyController.generateAccessKey(accessKeyDto1).getBody();
                    accessKeyController.generateAccessKey(accessKeyDto2).getBody();
                    List<AccessKeyDto> resList = accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();

                    assertEquals(2, resList.size());
                    assertCommonAccessKey(user, accessKeyDto1, resList.get(0), false);
                    assertCommonAccessKey(user, accessKeyDto2, resList.get(1), false, TEST_ACCESS_KEY2);
                });
    }

    /**
     * Getting access key value success flow.
     */
    @Test
    public void getAccessKey() {
        UserDto user = callCreateUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey), 
                () -> {
                    AccessKeyDto generatedKey = accessKeyController.generateAccessKey(accessKeyDto).getBody();
                    AccessKeyDto res = accessKeyController.getAccessKey(generatedKey.getId(), user.getId()).getBody();

                    assertCommonAccessKey(user, accessKeyDto, res, true);
                });
    }

    private void assertCommonAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result,
                                        boolean keyPresent) {
        assertCommonAccessKey(user, accessKeyDto, result, keyPresent, TEST_ACCESS_KEY);
    }

    private void assertCommonAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result, 
                                        boolean keyPresent, String keyName) {
        assertNotSame(accessKeyDto, result);
        assertEquals(accessKeyDto.getName(), keyName);
        assertEquals(accessKeyDto.getUserId(), user.getId());
        assertEquals(0L, (long) result.getRevisionVersion());
        if (keyPresent) {
            assertNotNull(result.getKey());
        } else {
            assertNull(result.getKey());
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
        accessKeyDto.setName(keyName);
        accessKeyDto.setUserId(user.getId());
        return accessKeyDto;
    }

}
