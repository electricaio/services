package io.electrica.user.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import io.electrica.user.repository.AccessKeyRepository;

@Component
public class AccessKeyService extends AbstractService<AccessKey> {

    private final AccessKeyRepository acccessKeyRepo;
    private final UserService userService;

    public AccessKeyService(AccessKeyRepository acccessKeyRepo, UserService userService) {
        this.acccessKeyRepo = acccessKeyRepo;
        this.userService = userService;
    }

    public List<AccessKey> findAllNonArchivedByUser(Long userId) {
        return acccessKeyRepo.findAllNonArchivedByUser(userId);
    }

    public AccessKey findByKeyAndUser(Long accessKeyId, Long userId) {
        return acccessKeyRepo.findByKeyAndUser(accessKeyId, userId).
                orElseThrow(() -> new EntityNotFoundServiceException(
                        String.format("No access key %s for user %s", accessKeyId, userId)));
    }

    @Override
    protected AccessKey executeCreate(AccessKey newEntity) {
        //TODO: Dozer issue with long converting, get rid of it, when it's fixed
        long id = Long.parseLong(String.valueOf(newEntity.getUser().getId()));
        User user = userService.findById(id, true);
        newEntity.setUser(user);
        newEntity.setAccessKey(generateKey());
        return getRepository().save(newEntity);
    }

    private String generateKey() {
        //Implement key generation here
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void executeUpdate(AccessKey merged, AccessKey update) {
        throw new NotImplementedException("");
    }

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

}
