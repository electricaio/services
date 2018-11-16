package io.electrica.user.service;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.dto.AccessKeyDtoService;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccessKeyServiceTest extends UserServiceApplicationTest {

    @Inject
    private AccessKeyDtoService accessKeyDtoService;

    @Inject
    private AccessKeyService accessKeyService;

    @Before
    public void init() {
        initBaseClass();
    }

    @Test
    public void validateUserAccessKeyByJti() {
        UserDto userDto = createAndSaveUser();

        AccessKeyDto accessKey = new AccessKeyDto();
        accessKey.setName("test");
        User user = new User();
        user.setId(userDto.getId());
        accessKey.setUserId(userDto.getId());
        accessKey = accessKeyDtoService.create(accessKey);

        FullAccessKeyDto fullAccessKeyDto = accessKeyDtoService.findByKey(accessKey.getId());
        Boolean exist = accessKeyService.validateUserAccessKeyByJti(fullAccessKeyDto.getJti(), userDto.getId());
        assertTrue(exist);
    }

    @Test
    public void validateUserAccessKeyByJtiThatDontExist() {
        UserDto user = createAndSaveUser();
        Boolean exist = accessKeyService.validateUserAccessKeyByJti(UUID.randomUUID(), user.getId());
        assertFalse(exist);
    }
}
