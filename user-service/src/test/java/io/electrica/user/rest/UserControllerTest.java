package io.electrica.user.rest;

import io.electrica.common.context.Identity;
import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForUser;
import io.electrica.test.context.IdentityContextTestHelper;
import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.Permission;
import io.electrica.user.model.User;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for user controller.
 */
@NoArgsConstructor
public class UserControllerTest extends UserServiceApplicationTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Resource
    private WebApplicationContext webApplicationContext;

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
    public void getUserTest() {
        UserDto userDto = createAndSaveUser();
        UserDto searchUser = userController.getUser(userDto.getId()).getBody();
        assertEquals(userDto.getId(), searchUser.getId());
        assertEquals(userDto.getEmail(), searchUser.getEmail());
        assertEquals(userDto.getRevisionVersion(), searchUser.getRevisionVersion());
        assertEquals(userDto.getFirstName(), searchUser.getFirstName());
        assertEquals(userDto.getLastName(), searchUser.getLastName());
        assertEquals(userDto.getUuid(), searchUser.getUuid());
        assertEquals(userDto.getOrganizationId(), searchUser.getOrganizationId());


    }

    @Test(expected = EntityNotFoundServiceException.class)
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
    public void getUsersForOrganizationTest() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        List<UserDto> userDtos = userController.getUsersForOrganization(u1.getOrganizationId()).getBody();
        assertEquals(2, userDtos.size());
        assertEquals(u1.getId(), userDtos.get(0).getId());
        assertEquals(u2.getId(), userDtos.get(1).getId());
    }

    @Test
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
    public void getUsersForOrganizationWithOrgNotExist() {
        UserDto u1 = createAndSaveUser();
        UserDto u2 = createAndSaveUser();
        List<UserDto> userDtos = userController.getUsersForOrganization(121212L).getBody();
        assertEquals(0, userDtos.size());
    }

    @Test
    public void getUsersForOrganizationWithNoUsers() {
        List<UserDto> userDtos = userController.getUsersForOrganization(defaultOrganization.getId()).getBody();
        assertEquals(0, userDtos.size());
    }

    private UserDto callCreateUser(CreateUserDto createUserDto) {
        ResponseEntity<UserDto> response = userController.createUser(createUserDto);
        return response.getBody();
    }

}
