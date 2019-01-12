package io.electrica.it.user;

import io.electrica.common.security.RoleType;
import io.electrica.it.BaseIT;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class AccessKeyServiceTest extends BaseIT {

    private UserDto user;

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        createAccessKey(user.getId(), user.getId() + getCurrTimeAsString());
    }

    @Test
    public void testFindAllNonArchivedByUser() {
        List<AccessKeyDto> accessKeyDtoList = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody();
        assertTrue(accessKeyDtoList.size() > 0);
    }

    @Test
    public void testGetAccessKey() {
        AccessKeyDto accessKeyDto = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        assertNotNull(fullAccessKeyDto.getJti());
        assertNotNull(fullAccessKeyDto.getKey());
        assertEquals(accessKeyDto.getId(), fullAccessKeyDto.getId());
        assertEquals(accessKeyDto.getRevisionVersion(), fullAccessKeyDto.getRevisionVersion());
    }

    @Test
    public void testRefreshAccessKey() {
        AccessKeyDto accessKeyDto = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        AccessKeyDto newAccessKeyDto = accessKeyClient.refreshAccessKey(accessKeyDto.getId()).getBody();
        FullAccessKeyDto newFullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        assertEquals(accessKeyDto.getId(), newAccessKeyDto.getId());
        assertNotEquals(fullAccessKeyDto.getJti(), newFullAccessKeyDto.getJti());
        assertNotEquals(fullAccessKeyDto.getKey(), newFullAccessKeyDto.getKey());
    }

    @Test
    public void testDeleteAccessKey() {
        CreateAccessKeyDto accessKeyDto = new CreateAccessKeyDto();
        accessKeyDto.setName("Test");
        accessKeyDto.setUserId(user.getId());
        AccessKeyDto accessKeyDto1 = accessKeyClient.createAccessKey(accessKeyDto).getBody();
        List<AccessKeyDto> accessKeyDtoList = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody();
        assertTrue(accessKeyDtoList.size() > 0);
        accessKeyClient.deleteAccessKey(accessKeyDto1.getId());
        List<AccessKeyDto> newAccessKeyDtoList = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody();
        assertEquals(accessKeyDtoList.size() - 1, newAccessKeyDtoList.size());
    }
}
