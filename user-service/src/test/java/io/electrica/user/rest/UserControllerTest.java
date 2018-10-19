package io.electrica.user.rest;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForUser;
import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for user controller.
 */
@NoArgsConstructor
public class UserControllerTest extends UserServiceApplicationTest {

    @Before
    public void init() {
        initBaseClass();
    }

    @Test
    public void createUserTest() {
        CreateUserDto createUserDto = createUserDto();
        UserDto result = callCreateUser(createUserDto);
        assertNotNull(result.getId());
        assertEquals(result.getEmail(), createUserDto.getEmail());
        assertEquals(result.getFirstName(), createUserDto.getFirstName());
        assertEquals(result.getLastName(), createUserDto.getLastName());
        assertNotNull(result.getRevisionVersion());
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = PermissionType.ReadUser)
    public void getUserTestWithSuperAdmin() {
        UserDto userDto = createAndSaveUser();
        UserDto searchUser = userController.getUser(userDto.getId()).getBody();
        equals(userDto, searchUser);
    }

    @Test
    public void getUserTestWithOrgAdminInSameOrgHavePermission() {
        UserDto userDto = createAndSaveUser();
        UserDto userDto2 = createAndSaveUser();
        executeForUser(userDto2.getId(), userDto2.getOrganizationId(), EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.ReadUser), () -> {
                    UserDto searchUser = userController.getUser(userDto.getId()).getBody();
                    equals(userDto, searchUser);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserTestWithOrgAdminInDiffOrgHavePermission() {
        UserDto userDto = createAndSaveUser();
        UserDto userDto2 = createAndSaveUser();
        executeForUser(userDto2.getId(), 0L, EnumSet.of(RoleType.OrgAdmin),
                EnumSet.of(PermissionType.ReadUser), () -> {
                    UserDto searchUser = userController.getUser(userDto.getId()).getBody();
                    equals(userDto, searchUser);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserTestWithOrgUserInSameOrgHavePermission() {
        UserDto userDto = createAndSaveUser();
        UserDto userDto2 = createAndSaveUser();
        executeForUser(userDto2.getId(), userDto2.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadUser), () -> {
                    userController.getUser(userDto.getId()).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void getUserTestWithOrgUserInDiffOrgHavePermission() {
        UserDto userDto = createAndSaveUser();
        UserDto userDto2 = createAndSaveUser();
        executeForUser(userDto2.getId(), 0L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadUser), () -> {
                    userController.getUser(userDto.getId()).getBody();
                });
    }

    @Test(expected = EntityNotFoundServiceException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = PermissionType.ReadUser)
    public void getUserTestWithIdDontExist() {
        userController.getUser(100L).getBody();
    }

    @Test
    public void saltedPasswordTest() {
        CreateUserDto createUserDto = createUserDto();
        UserDto result = callCreateUser(createUserDto);
        User saltedUSer = userService.findById(result.getId(), false);
        Assert.assertNotEquals(saltedUSer.getSaltedPassword(), createUserDto.getPassword());
        assertTrue(passwordEncoder.matches(createUserDto.getPassword(), saltedUSer.getSaltedPassword()));
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = PermissionType.ReadOrg)
    public void getUsersForOrganizationTest() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        List<UserDto> userDtos = userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
        assertEquals(2, userDtos.size());
        assertEquals(u1.getId(), userDtos.get(0).getId());
        assertEquals(u2.getId(), userDtos.get(1).getId());
    }

    @Test
    public void getUsersForOrganizationTestWithUserInSameOrg() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        executeForUser(u1.getId(), u1.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadOrg), () -> {
                    List<UserDto> userDtos = userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
                    assertEquals(2, userDtos.size());
                    assertEquals(u1.getId(), userDtos.get(0).getId());
                    assertEquals(u2.getId(), userDtos.get(1).getId());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void getUsersForOrganizationTestWithUserInDiffOrgThrowException() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        executeForUser(u1.getId(), 1L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadOrg), () -> {
                    userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
                });
    }


    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.AddPermission)
    public void getUsersForOrganizationTestWithWrongPermissionShouldThrowException() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        executeForUser(u1.getId(), u1.getOrganizationId(), EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.AddPermission), () -> {
                    userController.getUsersForOrganization(u1.getOrganizationId());
                });
    }

    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = PermissionType.ReadOrg)
    public void getUsersForOrganizationWithMultipleOrgsTest() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        OrganizationDto organizationDto = createAndSaveNewOrganization();
        UserDto u3 = createUserDto();
        u3.setOrganizationId(organizationDto.getId());
        List<UserDto> userDtos = userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
        assertEquals(2, userDtos.size());
        assertEquals(u1.getId(), userDtos.get(0).getId());
        assertEquals(u2.getId(), userDtos.get(1).getId());
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = PermissionType.ReadOrg)
    public void getUsersForOrganizationWithOrgNotExist() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        List<UserDto> userDtos = userController.getUsersForOrganization(121212L).getBody();
        assertEquals(0, userDtos.size());
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = PermissionType.ReadOrg)
    public void getUsersForOrganizationWithNoUsers() {
        List<UserDto> userDtos = userController.getUsersForOrganization(defaultOrganization.getId()).getBody();
        assertEquals(0, userDtos.size());
    }

    private UserDto callCreateUser(CreateUserDto createUserDto) {
        ResponseEntity<UserDto> response = userController.createUser(createUserDto);
        return response.getBody();
    }

}
