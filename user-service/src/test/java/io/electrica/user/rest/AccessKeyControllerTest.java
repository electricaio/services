package io.electrica.user.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForUser;
import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for access key controller.
 */
public class AccessKeyControllerTest extends UserServiceApplicationTest {

    private static final String TEST_ACCESS_KEY = "TestAccessKey";
    private static final String TEST_ACCESS_KEY2 = "TestAccessKey2";

    @Inject
    private AccessKeyController accessKeyController;

    @Before
    public void init() {
        initBaseClass();
    }

    /**
     * Generate access key success flow.
     */
    @Test
    public void generateAccessKey() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();

                    assertTestAccessKey(user, accessKeyDto, result);
                });
    }

    /**
     * Not for self user.
     */
    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.CreateAccessKey)
    public void generateAccessKeyNoAccessIncorrectUser() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        accessKeyController.createAccessKey(accessKeyDto).getBody();
    }

    /**
     * No Permission for key generation.
     */
    @Test(expected = AccessDeniedException.class)
    public void generateAccessKeyNoPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadOrg),
                () -> {
                    accessKeyController.createAccessKey(accessKeyDto).getBody();
                });
    }

    /**
     * Getting list of key success flow.
     */
    @Test
    public void findAllNonArchivedByUser() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto1 = createAccessKeyDto(user);
        AccessKeyDto accessKeyDto2 = createAccessKeyDto(user, TEST_ACCESS_KEY2);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    accessKeyController.createAccessKey(accessKeyDto1).getBody();
                    accessKeyController.createAccessKey(accessKeyDto2).getBody();
                    List<AccessKeyDto> resList = accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();

                    assertEquals(2, resList.size());
                    assertTestAccessKey(user, accessKeyDto1, resList.get(0));
                    assertAccessKey(user, accessKeyDto2, resList.get(1), TEST_ACCESS_KEY2);
                });
    }

    /**
     * Getting access key value success flow.
     */
    @Test
    public void getAccessKey() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto generatedKey = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    FullAccessKeyDto res = accessKeyController.getAccessKey(generatedKey.getId(), user.getId())
                            .getBody();

                    assertTestAccessKey(user, accessKeyDto, res);
                });
    }

    private void assertTestAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result) {
        assertAccessKey(user, accessKeyDto, result, TEST_ACCESS_KEY);
    }

    private void assertAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result, String keyName) {
        assertNotSame(accessKeyDto, result);
        assertEquals(accessKeyDto.getName(), keyName);
        assertEquals(accessKeyDto.getUserId(), user.getId());
        assertEquals(0L, (long) result.getRevisionVersion());
        if (result instanceof FullAccessKeyDto) {
            FullAccessKeyDto fullKey = (FullAccessKeyDto) result;
            assertNotNull(fullKey.getKey());
            assertNotNull(fullKey.getJti());
        }
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
