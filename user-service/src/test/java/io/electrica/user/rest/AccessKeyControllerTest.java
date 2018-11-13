package io.electrica.user.rest;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.repository.AccessKeyRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for access key controller.
 */
public class AccessKeyControllerTest extends UserServiceApplicationTest {

    private static final String TEST_ACCESS_KEY = "TestAccessKey";
    private static final String TEST_ACCESS_KEY2 = "TestAccessKey2";

    @Inject
    private AccessKeyControllerImpl accessKeyController;

    @Inject
    private AccessKeyRepository accessKeyRepository;

    @Before
    public void init() {
        initBaseClass();
    }

    /**
     * Generate access key success flow.
     */
    @Test
    public void createAccessKeyByOrgUserHasCreateAccessKeyPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                });
    }

    @Test
    public void createAccessKeyByOrgAdminHasCreateAccessKeyPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                });
    }

    @Test
    public void createAccessKeyBySuperAdminHasCreateAccessKeyPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.SuperAdmin),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void createAccessKeyByOrgAdminHasNoCreateAccessKeyPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.AddPermission),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void createAccessKeyByOrgUserHasNoCreateAccessKeyPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                });
    }


    @Test(expected = DataIntegrityViolationException.class)
    public void createAccessKeyByOrgUserHasCreateAccessKeyPermissionWithSameNameAlreadyExists() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                    accessKeyController.createAccessKey(accessKeyDto).getBody();
                });
        accessKeyRepository.flush();
    }

    @Test(expected = AccessDeniedException.class)
    public void createAccessKeyByOrgUserHasCreateAccessKeyPermissionWithAccessKeyCreatedByOtherUser() {
        UserDto user = createAndSaveUser();
        UserDto user2 = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AccessKeyDto accessKeyDto2 = createAccessKeyDto(user2);
        accessKeyDto2.setName(accessKeyDto.getName());
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                    accessKeyController.createAccessKey(accessKeyDto2).getBody();
                });
        accessKeyRepository.flush();
    }


    /**
     * Getting list of keys success flow.
     */
    @Test
    public void findAllNonArchivedByUser() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto1 = createAccessKeyDto(user, "Key1");
        AccessKeyDto accessKeyDto2 = createAccessKeyDto(user, "Key2");
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    accessKeyController.createAccessKey(accessKeyDto1).getBody();
                    accessKeyController.createAccessKey(accessKeyDto2).getBody();
                });


        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    List<AccessKeyDto> resList = accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();

                    assertEquals(2, resList.size());
                    assertTestAccessKey(user, accessKeyDto1, resList.get(0));
                    assertAccessKey(user, accessKeyDto2, resList.get(1));
                });
    }

    /**
     * Getting list of keys when list is empty.
     */
    @Test
    public void findAllNonArchivedByUserNoKeys() {
        UserDto user = createAndSaveUser();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    List<AccessKeyDto> resList = accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();

                    assertEquals(0, resList.size());
                });
    }


    @Test(expected = AccessDeniedException.class)
    public void findAllNonArchivedByUserNoKeysHasWrongPermissionWithOrgUser() {
        UserDto user = createAndSaveUser();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission),
                () -> {
                    accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void findAllNonArchivedByUserNoKeysHasWrongPermissionWithOrgAdmin() {
        UserDto user = createAndSaveUser();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.AddPermission),
                () -> {
                    accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();
                });
    }


    /**
     * Getting access key value success flow.
     */
    @Test
    public void getAccessKey() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        final AtomicLong id = new AtomicLong();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto generatedKey = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    id.set(generatedKey.getId());
                });


        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    FullAccessKeyDto res = accessKeyController.getAccessKey(id.get())
                            .getBody();
                    assertTestAccessKey(user, accessKeyDto, res);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void getAccessKeyBelongToDiffUser() {
        UserDto user = createAndSaveUser();
        UserDto user2 = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        final AtomicLong id = new AtomicLong();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto generatedKey = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    id.set(generatedKey.getId());
                });


        executeForUser(user2.getId(), user2.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    FullAccessKeyDto res = accessKeyController.getAccessKey(id.get())
                            .getBody();
                    assertTestAccessKey(user, accessKeyDto, res);
                });
    }

    /**
     * Getting access key value wrong access key id.
     */
    @Test(expected = EntityNotFoundServiceException.class)
    public void getAccessKeyWrongKeyId() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    FullAccessKeyDto res = accessKeyController.getAccessKey(1L)
                            .getBody();
                    assertTestAccessKey(user, accessKeyDto, res);
                });
    }


    @Test(expected = AccessDeniedException.class)
    public void getAccessKeyWrongKeyIdWithNoReadPermissionForOrgUser() {
        UserDto user = createAndSaveUser();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission),
                () -> accessKeyController.getAccessKey(1L));
    }

    @Test(expected = AccessDeniedException.class)
    public void getAccessKeyWrongKeyIdWithNoReadPermissionForOrgAdmin() {
        UserDto user = createAndSaveUser();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.AddPermission),
                () -> accessKeyController.getAccessKey(1L));
    }


    @Test
    public void testRefreshKey() {
        UserDto user = createAndSaveUser();
        Long accessKeyId = createAndSaveAccessKey(user).getId();
        AtomicReference<FullAccessKeyDto> key = getFullAccessKeyDtoForKey(user, accessKeyId);

        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.refreshAccessKey(accessKeyId.longValue()).getBody();
                    assertEquals(key.get().getId(), result.getId());
                    assertEquals(key.get().getName(), result.getName());
                });
        AtomicReference<FullAccessKeyDto> key2 = getFullAccessKeyDtoForKey(user, accessKeyId);
        assertNotEquals(key.get().getKey(), key2.get().getKey());
        assertNotEquals(key.get().getJti(), key2.get().getJti());

    }

    @Test(expected = AccessDeniedException.class)
    public void testRefreshKeyBelongToDiffUser() {
        UserDto user = createAndSaveUser();
        UserDto user2 = createAndSaveUser();
        Long accessKeyId = createAndSaveAccessKey(user).getId();
        AtomicReference<FullAccessKeyDto> key = getFullAccessKeyDtoForKey(user, accessKeyId);

        executeForUser(user2.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> accessKeyController.refreshAccessKey(accessKeyId.longValue()).getBody());
    }

    @Test(expected = AccessDeniedException.class)
    public void testRefreshKeyWithWrongPermission() {
        UserDto user = createAndSaveUser();
        UserDto user2 = createAndSaveUser();
        Long accessKeyId = createAndSaveAccessKey(user).getId();
        AtomicReference<FullAccessKeyDto> key = getFullAccessKeyDtoForKey(user, accessKeyId);

        executeForUser(user2.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> accessKeyController.refreshAccessKey(accessKeyId.longValue()).getBody());
    }


    @Test
    public void testValidateAccessKey() {
        UserDto user = createAndSaveUser();
        Long accessKeyId = createAndSaveAccessKey(user).getId();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> assertTrue(accessKeyController.validateMyAccessKeyById(accessKeyId.longValue()).getBody()));
    }

    @Test
    public void testValidateAccessKeyOwnedByDiffUser() {
        UserDto user = createAndSaveUser();
        UserDto user2 = createAndSaveUser();
        Long accessKeyId = createAndSaveAccessKey(user2).getId();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> assertFalse(accessKeyController.validateMyAccessKeyById(accessKeyId.longValue()).getBody()));
    }

    @Test(expected = AccessDeniedException.class)
    public void testValidateAccessKeyWithoutReadPermission() {
        UserDto user = createAndSaveUser();
        Long accessKeyId = createAndSaveAccessKey(user).getId();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission),
                () -> assertTrue(accessKeyController.validateMyAccessKeyById(accessKeyId.longValue()).getBody()));
    }

    @Test
    public void testDeleteAccessKeySuccess() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AccessKeyDto accessKeyDto2 = createAccessKeyDto(user);
        AtomicLong accessKeyId = new AtomicLong();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    accessKeyId.set(result.getId());
                    assertTestAccessKey(user, accessKeyDto, result);
                    accessKeyController.createAccessKey(accessKeyDto2).getBody();
                });
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.DeleteAccessKey),
                () -> {
                    HttpStatus statusCode = accessKeyController.deleteAccessKey(accessKeyId.get()).getStatusCode();
                    assertEquals(statusCode, HttpStatus.OK);
                });
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    List<AccessKeyDto> accessKeyDtoList = accessKeyController.findAllNonArchivedByUser(
                            user.getId()).getBody();
                    assertEquals(accessKeyDtoList.size(), 1);
                    AccessKeyDto result = accessKeyDtoList.get(0);
                    assertEquals(accessKeyDto2.getName(), result.getName());
                    assertEquals(accessKeyDto2.getUserId(), result.getUserId());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeleteAccessKeyWithoutDeleteAccessKeyPermission() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AtomicLong accessKeyId = new AtomicLong();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    accessKeyId.set(result.getId());
                    assertTestAccessKey(user, accessKeyDto, result);
                });
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission),
                () -> {
                    HttpStatus statusCode = accessKeyController.deleteAccessKey(accessKeyId.get()).getStatusCode();
                    assertEquals(statusCode, HttpStatus.OK);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeleteAccessKeyBelongtoDiffUser() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AtomicLong accessKeyId = new AtomicLong();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    accessKeyId.set(result.getId());
                    assertTestAccessKey(user, accessKeyDto, result);
                });
        executeForUser(2, user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.DeleteAccessKey),
                () -> {
                    HttpStatus statusCode = accessKeyController.deleteAccessKey(accessKeyId.get()).getStatusCode();
                    assertEquals(statusCode, HttpStatus.OK);
                });
    }

    @Test
    public void testDeleteAccessKeyBelongtoDiffUserButSuperAdmin() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AtomicLong accessKeyId = new AtomicLong();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    accessKeyId.set(result.getId());
                    assertTestAccessKey(user, accessKeyDto, result);
                });
        executeForUser(2, user.getOrganizationId(), EnumSet.of(RoleType.SuperAdmin),
                EnumSet.of(PermissionType.DeleteAccessKey),
                () -> {
                    HttpStatus statusCode = accessKeyController.deleteAccessKey(accessKeyId.get()).getStatusCode();
                    assertEquals(statusCode, HttpStatus.OK);
                });
    }

    private AtomicReference<FullAccessKeyDto> getFullAccessKeyDtoForKey(UserDto user, Long accessKeyId) {
        AtomicReference<FullAccessKeyDto> key = new AtomicReference<>();

        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    FullAccessKeyDto result = accessKeyController.getAccessKey(accessKeyId.longValue()).getBody();
                    key.set(result);

                });
        return key;
    }

    private AccessKeyDto createAndSaveAccessKey(UserDto user) {
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AtomicReference<AccessKeyDto> accessKey = new AtomicReference<>();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    accessKey.set(result);
                });
        return accessKey.get();
    }

    private void assertTestAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result) {
        assertAccessKey(user, accessKeyDto, result);
    }

    private void assertAccessKey(UserDto user, AccessKeyDto accessKeyDto, AccessKeyDto result) {
        assertNotSame(accessKeyDto, result);
        assertEquals(accessKeyDto.getName(), result.getName());
        assertEquals(accessKeyDto.getUserId(), user.getId());
        if (result instanceof FullAccessKeyDto) {
            FullAccessKeyDto fullKey = (FullAccessKeyDto) result;
            assertNotNull(fullKey.getKey());
            assertNotNull(fullKey.getJti());
        }
    }

    private AccessKeyDto createAccessKeyDto(UserDto user) {
        return createAccessKeyDto(user, TEST_ACCESS_KEY + new Random().nextInt(100));
    }

    private AccessKeyDto createAccessKeyDto(UserDto user, String keyName) {
        AccessKeyDto accessKeyDto = new AccessKeyDto();
        accessKeyDto.setName(keyName);
        accessKeyDto.setUserId(user.getId());
        return accessKeyDto;
    }

}
