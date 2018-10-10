package io.electrica.common.jpa.service;

public interface UpdateValidator<E> {
    void validate(E merged, E update);
}
