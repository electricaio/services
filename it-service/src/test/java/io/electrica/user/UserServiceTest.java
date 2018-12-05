package io.electrica.user;

import io.electrica.BaseIT;
import io.electrica.common.security.RoleType;
import io.electrica.it.auth.TokenDetails;
import io.electrica.it.context.SessionContextHolder;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class UserServiceTest extends BaseIT {

    @BeforeClass
    public void setup() {
        SessionContextHolder.getInstance().clear();
    }

    @Test(groups = {"fillData"})
    public void testAddOrganizations() {
        OrganizationDto orgHackerRank = new OrganizationDto();
        orgHackerRank.setName(ORG_HACKER_RANK);
        OrganizationDto entity = organizationClient.createIfAbsent(orgHackerRank).getBody();
        assertEquals(entity.getName(), ORG_HACKER_RANK);
        SessionContextHolder.getInstance().addOrganizationToContext(entity);

        OrganizationDto orgTopCoder = new OrganizationDto();
        orgTopCoder.setName(ORG_TOP_CODER);
        entity = organizationClient.createIfAbsent(orgTopCoder).getBody();
        assertEquals(entity.getName(), ORG_TOP_CODER);
        SessionContextHolder.getInstance().addOrganizationToContext(entity);

        assertEquals(2, SessionContextHolder.getInstance().getOrganizations().size());
    }


    @Test(groups = {"fillData"}, dependsOnMethods = {"testAddOrganizations"})
    public void testAddUsersToOrganizations() {

        UserDto inderSabharwal = createUser("inder", "inder@electrica.io", ORG_HACKER_RANK, RoleType.OrgUser);
        UserDto aleksey = createUser("aleksey", "aleksey@electrica.io", ORG_HACKER_RANK, RoleType.OrgUser);
        UserDto munish = createUser("munish", "munish@electrica.io", ORG_TOP_CODER, RoleType.OrgUser);
        UserDto chris = createUser("chris", "chris@electrica.io", ORG_TOP_CODER, RoleType.OrgUser);

        assertEquals(2, SessionContextHolder.getInstance().getOrganizationByName(ORG_HACKER_RANK).getUserMap().size());
        assertEquals(2, SessionContextHolder.getInstance().getOrganizationByName(ORG_TOP_CODER).getUserMap().size());
    }

    @Test(groups = {"test"}, dependsOnGroups = {"init", "fillData"})
    public void testLogin() {
        TokenDetails tokenDetails = tokenManager.getTokenDetailsForUser("inder@electrica.io", "inder");
        assertNotNull(tokenDetails.getAccessToken());
        assertNotNull(tokenDetails.getRefreshToken());
        assertNotNull(tokenDetails.getCreatedDateTime());
        assertNotNull(tokenDetails.getExpiresIn());
        assertNotNull(tokenDetails.getJti());
    }


    private UserDto createUser(String username, String email, String org, RoleType roleType) {
        Long orgId = SessionContextHolder.getInstance().getOrganizationByName(org)
                .getOrganizationDto().getId();
        CreateUserDto user = new CreateUserDto();
        user.setOrganizationId(orgId);
        user.setFirstName(username);
        user.setLastName(username);
        user.setEmail(email);
        user.setLastName(username);
        user.setPassword(username);
        UserDto userDto = userClient.createUser(user).getBody();
        SessionContextHolder.getInstance().addUserToOrganization(org, userDto);
        return userDto;
    }

}
