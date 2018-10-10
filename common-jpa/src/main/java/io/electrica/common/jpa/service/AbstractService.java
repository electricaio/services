package io.electrica.common.jpa.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.jpa.model.CommonEntity;
import io.electrica.common.jpa.service.validation.StringFieldValidationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Objects;

public abstract class AbstractService<E extends CommonEntity> {

    @Inject
    private StringFieldValidationService stringFieldValidationService;

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

    @Transactional
    public E create(E newEntity) {
        if (!newEntity.isNew()) {
            throw new BadRequestServiceException("Use update for existing entity");
        }
        if (newEntity.getRevisionVersion() != null) {
            throw new BadRequestServiceException("Version can't be specified");
        }
        validate(null, newEntity, true);
        newEntity.setArchived(false);
        return executeCreate(newEntity);
    }

    private void validate(E merged, E update, boolean create) {
        stringFieldValidationService.validate(update);
        executeValidate(merged, update, create);
    }

    /**
     * If {@code create == true} then {@code merged} is null and {@code update} is new entity.
     */
    protected void executeValidate(E merged, E update, boolean create) {
    }

    protected abstract E executeCreate(E newEntity);

    public E update(long id, E update) {
        return update(id, update, null);
    }

    @Transactional
    public E update(long id, E update, UpdateValidator<E> validator) {
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
        validate(merged, update, false);
        if (validator != null) {
            validator.validate(merged, update);
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
