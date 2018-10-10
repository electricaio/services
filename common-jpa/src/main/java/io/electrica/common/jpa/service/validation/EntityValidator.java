package io.electrica.common.jpa.service.validation;

public interface EntityValidator<E> {

    void validateCreate(E create);

    default void validateUpdate(E merged, E update) {
        // use the same validation as for entity create by default
        validateCreate(update);
    }
}
