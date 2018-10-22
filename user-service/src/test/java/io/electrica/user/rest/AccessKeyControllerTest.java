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
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

/**
 * Tests for access key controller.
 */
public class AccessKeyControllerTest extends UserServiceApplicationTest {

    private static final String TEST_ACCESS_KEY = "TestAccessKey";
    private static final String TEST_ACCESS_KEY2 = "TestAccessKey2";

    @Inject
    private AccessKeyController accessKeyController;

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

    @Test
    public void createAccessKeyByOrgUserHasCreateAccessKeyPermissionWithAccessKeyCreatedByOtherUser() {
        UserDto user = createAndSaveUser();
        UserDto user2 = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        AccessKeyDto accessKeyDto_copy = createAccessKeyDto(user2);
        accessKeyDto_copy.setName(accessKeyDto.getName());
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateAccessKey),
                () -> {
                    AccessKeyDto result = accessKeyController.createAccessKey(accessKeyDto).getBody();
                    assertTestAccessKey(user, accessKeyDto, result);
                    accessKeyController.createAccessKey(accessKeyDto_copy).getBody();
                });
        accessKeyRepository.flush();
    }


    /**
     * Getting list of keys success flow.
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
                });


        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadAccessKey),
                () -> {
                    List<AccessKeyDto> resList = accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();

                    assertEquals(2, resList.size());
                    assertTestAccessKey(user, accessKeyDto1, resList.get(0));
                    assertAccessKey(user, accessKeyDto2, resList.get(1), TEST_ACCESS_KEY2);
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
                EnumSet.of(PermissionType.CreateSTL),
                () -> {
                    accessKeyController.findAllNonArchivedByUser(user.getId()).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void findAllNonArchivedByUserNoKeysHasWrongPermissionWithOrgAdmin() {
        UserDto user = createAndSaveUser();
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.CreateSTL),
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
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission),
                () -> accessKeyController.getAccessKey(1L));
    }

    @Test(expected = AccessDeniedException.class)
    public void getAccessKeyWrongKeyIdWithNoReadPermissionForOrgAdmin() {
        UserDto user = createAndSaveUser();
        AccessKeyDto accessKeyDto = createAccessKeyDto(user);
        executeForUser(user.getId(), user.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.AddPermission),
                () -> accessKeyController.getAccessKey(1L));
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
