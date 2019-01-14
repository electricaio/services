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
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class AccessKeyServiceTest extends BaseIT {

    private UserDto user;
    private AccessKeyDto accessKeyDto;

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        accessKeyDto = createAccessKey(user.getId(), user.getId() + getCurrTimeAsString());
    }

    @Test
    public void testFindAllNonArchivedByUser() {
        List<AccessKeyDto> accessKeyDtoList = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody();
        assertEquals(1, accessKeyDtoList.size());
    }

    @Test
    public void testGetAccessKey() {
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        assertNotNull(fullAccessKeyDto.getJti());
        assertNotNull(fullAccessKeyDto.getKey());
        compareAccessKey(accessKeyDto, fullAccessKeyDto);
    }

    @Test
    public void testRefreshAccessKey() {
        AccessKeyDto accessKeyDto = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        AccessKeyDto newAccessKeyDto = accessKeyClient.refreshAccessKey(accessKeyDto.getId()).getBody();
        FullAccessKeyDto newFullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        assertNotEquals(fullAccessKeyDto.getJti(), newFullAccessKeyDto.getJti());
        assertNotEquals(fullAccessKeyDto.getKey(), newFullAccessKeyDto.getKey());
        compareAccessKey(accessKeyDto, newAccessKeyDto);
    }

    @Test
    public void testDeleteAccessKey() {
        CreateAccessKeyDto accessKeyDto = new CreateAccessKeyDto();
        accessKeyDto.setName("Test");
        accessKeyDto.setUserId(user.getId());
        int accessKeysCount = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().size();
        AccessKeyDto newAccessKey = accessKeyClient.createAccessKey(accessKeyDto).getBody();
        int accessKeysCountAfterInsert = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().size();
        assertEquals(accessKeysCount + 1, accessKeysCountAfterInsert);
        accessKeyClient.deleteAccessKey(newAccessKey.getId());
        List<AccessKeyDto> accessKeyDtoList = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody();
        Optional<AccessKeyDto> optionalAccessKeyDto = accessKeyDtoList.stream()
                .filter(a -> Objects.equals(a.getId(), newAccessKey.getId()))
                .findAny();
        assertFalse(optionalAccessKeyDto.isPresent());
        assertEquals(accessKeysCount, accessKeyDtoList.size());
    }

    private void compareAccessKey(AccessKeyDto accessKeyDto, AccessKeyDto newAccessKeyDto) {
        assertEquals(accessKeyDto.getName(), newAccessKeyDto.getName());
        assertEquals(accessKeyDto.getUserId(), newAccessKeyDto.getUserId());
        assertEquals(accessKeyDto.getId(), newAccessKeyDto.getId());
        assertEquals(accessKeyDto.getCreatedAt(), newAccessKeyDto.getCreatedAt());
    }
}
