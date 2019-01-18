package io.electrica.it.user;

import io.electrica.common.security.RoleType;
import io.electrica.it.BaseIT;
import io.electrica.it.auth.TokenDetails;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest extends BaseIT {

    private UserDto user;

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setTokenForUser(user.getEmail());
    }

    @Test
    void testAddOrganizations() {
        OrganizationDto org = createOrganization(ORG_TOP_CODER);
        assertAll("organization",
                () -> assertNotNull(org),
                () -> assertNotNull(org.getId()),
                () -> assertEquals(org.getName(), ORG_TOP_CODER),
                () -> assertNotNull(org.getRevisionVersion()));
    }


    @Test
    void testAddUsersToOrganizations() {
        assertAll("createUseTest",
                () -> assertTrue(contextHolder.getUsers().size() > 0),
                () -> assertNotNull(user)
        );
    }

    @Test
    void testAddAccessKeyToUser() {
        createAccessKey(user.getId(), "Test");
    }

    @Test
    void testLogin() {
        TokenDetails tokenDetails = tokenManager.getTokenDetailsForUser(user.getEmail(), user.getFirstName());
        assertNotNull(tokenDetails.getAccessToken());
        assertNotNull(tokenDetails.getRefreshToken());
        assertNotNull(tokenDetails.getCreatedDateTime());
        assertNotNull(tokenDetails.getExpiresIn());
        assertNotNull(tokenDetails.getJti());
    }

    @Test
    void testRefreshToken() {
        TokenDetails tokenDetails = tokenManager.getTokenDetailsForUser(user.getEmail(), user.getFirstName());
        TokenDetails refreshTokenDetail = tokenManager.getNewAccessTokenFromRefreshToken(tokenDetails);
        assertNotEquals(refreshTokenDetail.getAccessToken(), tokenDetails.getAccessToken());
        assertNotEquals(tokenDetails.getRefreshToken(), refreshTokenDetail.getRefreshToken());
        assertNotEquals(tokenDetails.getJti(), refreshTokenDetail.getJti());
    }

}
