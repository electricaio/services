package io.electrica.common.jpa.service.validation;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ContainerValidatorStorage {

    private final Map<String, ContainerEntityValidator> validatorsById = new HashMap<>();

    @Inject
    public ContainerValidatorStorage(List<ContainerEntityValidator> validators) {
        validators.forEach(validator -> {
            ContainerEntityValidator prev = validatorsById.put(validator.getId(), validator);
            if (prev != null) {
                throw new IllegalStateException("Duplicate validator for id: " + validator.getId());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <E> EntityValidator<E> get(String id) {
        ContainerEntityValidator validator = validatorsById.get(id);
        if (validator == null) {
            throw new IllegalArgumentException("Unknown validator: " + id);
        }
        return (EntityValidator<E>) validator;
    }
}
