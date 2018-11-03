package io.electrica.user.service;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccessKeyServiceTest extends UserServiceApplicationTest {

    @Inject
    private AccessKeyService accessKeyService;

    @Before
    public void init() {
        initBaseClass();
    }

    @Test
    public void validateUserAccessKeyByJti() {
        UserDto userDto = createAndSaveUser();

        AccessKey accessKey = new AccessKey();
        accessKey.setName("test");
        User user = new User();
        user.setId(userDto.getId());
        accessKey.setUser(user);
        accessKey = accessKeyService.create(accessKey);

        Boolean exist = accessKeyService.validateUserAccessKeyByJti(accessKey.getJti(), userDto.getId());
        assertTrue(exist);
    }

    @Test
    public void validateUserAccessKeyByJtiThatDontExist() {
        UserDto user = createAndSaveUser();
        Boolean exist = accessKeyService.validateUserAccessKeyByJti(UUID.randomUUID(), user.getId());
        assertFalse(exist);
    }
}
