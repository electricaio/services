package io.electrica.it.user;

import io.electrica.common.security.RoleType;
import io.electrica.it.BaseIT;
import io.electrica.it.auth.TokenDetails;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

public class UserServiceTest extends BaseIT {

    private static final String USER_NAME_PREFIX = "user_";
    private static final String EMAIL_POSTFIX = "@electrica.io";

    @BeforeClass
    public void setup() {
        contextHolder.clear();
    }

    @Test(groups = {"fillData"})
    public void testAddOrganizations() {
        createOrganization(ORG_HACKER_RANK);
        createOrganization(ORG_TOP_CODER);

        assertEquals(2, contextHolder.getOrganizations().size());
        assertEquals(ORG_HACKER_RANK, contextHolder.getOrganizations().get(0).getName());
        assertEquals(ORG_TOP_CODER, contextHolder.getOrganizations().get(1).getName());
    }


    @Test(groups = {"fillData"}, dependsOnMethods = {"testAddOrganizations"})
    public void testAddUsersToOrganizations() {

        createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        createUser(ORG_TOP_CODER, RoleType.OrgUser);
        createUser(ORG_TOP_CODER, RoleType.OrgUser);
        assertEquals(4, contextHolder.getUsers().size());

    }

    @Test(groups = {"fillData"}, dependsOnMethods = {"testAddUsersToOrganizations"})
    public void testAddAccessKeyToUser() {
        contextHolder.getUsers().stream()
                .forEach(u -> {
                    contextHolder.setContextForUser(u.getEmail());
                    createAccessKey(u.getId(), "Development");
                    createAccessKey(u.getId(), "Staging");
                    createAccessKey(u.getId(), "Production");
                });
    }

    @Test(groups = {"test"}, dependsOnGroups = {"init", "fillData"})
    public void testLogin() {
        UserDto user = contextHolder.getUsers().get(0);
        TokenDetails tokenDetails = tokenManager.getTokenDetailsForUser(user.getEmail(), user.getFirstName());
        assertNotNull(tokenDetails.getAccessToken());
        assertNotNull(tokenDetails.getRefreshToken());
        assertNotNull(tokenDetails.getCreatedDateTime());
        assertNotNull(tokenDetails.getExpiresIn());
        assertNotNull(tokenDetails.getJti());
    }


    private UserDto createUser(String org, RoleType roleType) {
        String name = USER_NAME_PREFIX + new Date().getTime();
        Long orgId = contextHolder.getOrganizationByName(org).getId();
        CreateUserDto user = new CreateUserDto();
        user.setOrganizationId(orgId);
        user.setFirstName(name);
        user.setLastName(name);
        user.setEmail(name + EMAIL_POSTFIX);
        user.setLastName(name);
        user.setPassword(name);
        UserDto userDto = userClient.createUser(user).getBody();
        contextHolder.addUserToContext(userDto);
        return userDto;
    }

    private void createAccessKey(Long userId, String name) {
        CreateAccessKeyDto accessKeyDto = new CreateAccessKeyDto();
        accessKeyDto.setName(name);
        accessKeyDto.setUserId(userId);
        accessKeyClient.createAccessKey(accessKeyDto);
    }

    private OrganizationDto createOrganization(String name) {
        OrganizationDto orgTopCoder = new OrganizationDto();
        orgTopCoder.setName(name);
        OrganizationDto entity = organizationClient.createIfAbsent(orgTopCoder).getBody();
        contextHolder.addOrganizationToContext(entity);
        return entity;
    }

}
