package io.electrica.common.jpa.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.helper.ValueCache;
import io.electrica.common.jpa.model.CommonEntity;
import io.electrica.common.jpa.service.validation.ContainerValidatorStorage;
import io.electrica.common.jpa.service.validation.EntityValidator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends CommonEntity> {

    @Inject
    private ContainerValidatorStorage containerValidatorStorage;

    private final ValueCache<List<EntityValidator<E>>> validators = new ValueCache<>(() -> {
        List<EntityValidator<E>> result = new ArrayList<>();
        result.addAll(getContainerValidators().stream()
                .map((Function<String, EntityValidator<E>>) id -> containerValidatorStorage.get(id))
                .collect(Collectors.toList())
        );
        result.addAll(getValidators());
        return result;
    });

    private static void throwEntityNotFound(long id, boolean hideArchived) {
        throw new EntityNotFoundServiceException(String.format(
                "Id %d not found %s.", id,
                hideArchived ? "(except archived)" : "(including archived)"
        ));
    }

    @Transactional
    public E findById(long id, boolean hideArchived) {
        JpaRepository<E, Long> repository = getRepository();
        E result = repository.findById(id).orElse(null);
        if (result == null || hideArchived && result.getArchived()) {
            throwEntityNotFound(id, hideArchived);
        }
        return result;
    }

    public E create(E newEntity) {
        return create(newEntity, null);
    }

    @Transactional
    public E create(E newEntity, @Nullable EntityValidator<E> validator) {
        if (!newEntity.isNew()) {
            throw new BadRequestServiceException("Use update for existing entity");
        }
        if (newEntity.getRevisionVersion() != null) {
            throw new BadRequestServiceException("Version can't be specified");
        }

        // validation
        validators.get().forEach(v -> v.validateCreate(newEntity));
        if (validator != null) {
            validator.validateCreate(newEntity);
        }

        return executeCreate(newEntity);
    }

    protected abstract Collection<String> getContainerValidators();

    protected abstract Collection<EntityValidator<E>> getValidators();

    protected abstract E executeCreate(E newEntity);

    public E update(long id, E update) {
        return update(id, update, null);
    }

    @Transactional
    public E update(long id, E update, @Nullable EntityValidator<E> validator) {
        if (!Objects.equals(id, update.getId())) {
            throw new BadRequestServiceException(String.format(
                    "Id specified by path (%s) don't equal id in entity (%s)",
                    id, update.getId()
            ));
        }
        Long oldRevisionVersion = update.getRevisionVersion();
        if (oldRevisionVersion == null) {
            throw new BadRequestServiceException("Version must be specified for update");
        }
        E merged = findById(id, true);
        if (!Objects.equals(oldRevisionVersion, merged.getRevisionVersion())) {
            throw new ObjectOptimisticLockingFailureException(merged.getClass(), id);
        }

        // validation
        validators.get().forEach(v -> v.validateUpdate(merged, update));
        if (validator != null) {
            validator.validateUpdate(merged, update);
        }

        executeUpdate(merged, update);
        return merged;
    }

    protected abstract void executeUpdate(E merged, E update);

    @Transactional
    public E archive(long id) {
        E result = findById(id, true);
        result.setArchived(true);
        return result;
    }

    @Transactional
    public E unArchive(long id) {
        E result = findById(id, false);
        result.setArchived(false);
        return result;
    }

    public void delete(long id) {
        getRepository().deleteById(id);
    }

    protected abstract JpaRepository<E, Long> getRepository();

}
