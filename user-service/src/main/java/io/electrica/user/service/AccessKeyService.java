package io.electrica.user.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import io.electrica.user.repository.AccessKeyRepository;

@Component
public class AccessKeyService extends AbstractService<AccessKey> {

    @Inject
    private AccessKeyRepository acccessKeyRepo;
    @Inject
    private UserService userService;

    @Override
    protected Collection<String> getContainerValidators() {
        return Arrays.asList(
                ContainerEntityValidator.TRIMMED_STRINGS,
                ContainerEntityValidator.AVOID_EMTPY_STRINGS
        );
    }

    @Override
    protected Collection<EntityValidator<AccessKey>> getValidators() {
        return Collections.emptyList();
    }

    @Override
    protected JpaRepository<AccessKey, Long> getRepository() {
        return acccessKeyRepo;
    }

    @Override
    protected AccessKey executeCreate(AccessKey newEntity) {
        long id = Long.parseLong(String.valueOf(newEntity.getUser().getId()));
        User user = userService.findById(id, true);
        newEntity.setUser(user);
        newEntity.setAccessKey(generateKey());
        return getRepository().save(newEntity);
    }

    @Override
    protected void executeUpdate(AccessKey merged, AccessKey update) {
        throw new NotImplementedException("");
    }

    private String generateKey() {
        //Implement key generation here
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }
}
