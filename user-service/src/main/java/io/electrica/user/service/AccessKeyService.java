package io.electrica.user.service;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import io.electrica.user.repository.AccessKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccessKeyService extends AbstractService<AccessKey> {

    private final AccessKeyRepository accessKeyRepository;
    private final AccessKeyGenerator accessKeyGenerator;

    public AccessKeyService(AccessKeyRepository accessKeyRepository, AccessKeyGenerator accessKeyGenerator) {
        this.accessKeyRepository = accessKeyRepository;
        this.accessKeyGenerator = accessKeyGenerator;
    }

    List<AccessKey> findAllNonArchivedByUser(Long userId) {
        return accessKeyRepository.findAllNonArchivedByUser(userId);
    }

    AccessKey findByIdAndUser(Long accessKeyId, Long userId) {
        return accessKeyRepository.findByKeyAndUser(accessKeyId, userId).
                orElseThrow(() -> new EntityNotFoundServiceException(
                        String.format("No access key %s for user %s", accessKeyId, userId)));
    }

    AccessKey refreshKey(Long accessKeyId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        AccessKey accessKey = findById(accessKeyId, true);
        if (!(user.getId().equals(accessKey.getUser().getId()))) {
            throw new AccessDeniedException(
                    String.format("AccessKey %s belongs to another user", accessKeyId));
        }
        fillAccessKeyInfo(accessKey);
        return accessKeyRepository.saveAndFlush(accessKey);
    }

    @Override
    protected AccessKey executeCreate(AccessKey newEntity) {
        Long userId = newEntity.getUser().getId();
        User user = getReference(User.class, userId);
        newEntity.setUser(user);

        fillAccessKeyInfo(newEntity);
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

    private void fillAccessKeyInfo(AccessKey accessKey) {
        AccessKeyGenerator.Key key = accessKeyGenerator.createAccessKey(accessKey.getUser().getId());
        accessKey.setJti(key.getJti());
        accessKey.setKey(key.getValue());
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
