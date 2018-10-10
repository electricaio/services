package io.electrica.common.jpa.service.validation;

public interface ContainerEntityValidator extends EntityValidator<Object> {

    String AVOID_EMTPY_STRINGS = "avoid-empty-strings";
    String TRIMMED_STRINGS = "trimmed-strings";

    String getId();

}
