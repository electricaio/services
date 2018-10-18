package io.electrica.user.service;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import io.electrica.user.repository.AccessKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class AccessKeyService extends AbstractService<AccessKey> {

    private final AccessKeyRepository accessKeyRepository;

    public AccessKeyService(AccessKeyRepository accessKeyRepository) {
        this.accessKeyRepository = accessKeyRepository;
    }

    public List<AccessKey> findAllNonArchivedByUser(Long userId) {
        return accessKeyRepository.findAllNonArchivedByUser(userId);
    }

    public AccessKey findByIdAndUser(Long accessKeyId, Long userId) {
        return accessKeyRepository.findByKeyAndUser(accessKeyId, userId).
                orElseThrow(() -> new EntityNotFoundServiceException(
                        String.format("No access key %s for user %s", accessKeyId, userId)));
    }

    @Override
    protected AccessKey executeCreate(AccessKey newEntity) {
        Long userId = newEntity.getUser().getId();
        User user = getReference(User.class, userId);
        newEntity.setUser(user);

        fillAccessKey(newEntity);
        return getRepository().save(newEntity);
    }

    @Override
    protected EntityValidator<AccessKey> getValidator() {
        return create -> {
            // ToDo validate name

            List<String> required = new ArrayList<>();

            User user = create.getUser();
            if (user == null) {
                required.add("user");
            } else if (user.getId() == null) {
                required.add("user.id");
            }

            if (!required.isEmpty()) {
                throw new IllegalArgumentException("Required fields: " + String.join(", ", required));
            }
        };
    }

    private void fillAccessKey(AccessKey accessKey) {
        // TODO: Implement key generation here
        UUID uuid = UUID.randomUUID();
        String key = Base64.getEncoder().encodeToString(uuid.toString().getBytes(StandardCharsets.UTF_8));

        accessKey.setUuid(uuid);
        accessKey.setKey(key);
    }

    @Override
    protected void executeUpdate(AccessKey merged, AccessKey update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<AccessKey, Long> getRepository() {
        return accessKeyRepository;
    }

}
