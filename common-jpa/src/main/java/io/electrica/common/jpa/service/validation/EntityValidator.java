package io.electrica.common.jpa.service.validation;

import java.util.Collection;

public interface EntityValidator<E> {

    EntityValidator NOP = create -> {
        // do nothing
    };

    void validateCreate(E create);

    default void validateUpdate(E merged, E update) {
        // use the same validation as for entity create by default
        validateCreate(update);
    }

    class Chain<E> implements EntityValidator<E> {

        private final Collection<EntityValidator<E>> validators;

        public Chain(Collection<EntityValidator<E>> set) {
            this.validators = set;
        }

        @Override
        public void validateCreate(E create) {
            validators.forEach(v -> v.validateCreate(create));
        }

        @Override
        public void validateUpdate(E merged, E update) {
            validators.forEach(v -> v.validateUpdate(merged, update));
        }
    }
}
