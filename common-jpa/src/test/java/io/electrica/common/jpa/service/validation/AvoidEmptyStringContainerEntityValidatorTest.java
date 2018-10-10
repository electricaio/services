package io.electrica.common.jpa.service.validation;

import io.electrica.common.exception.BadRequestServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Column;
import java.util.Collections;

public class AvoidEmptyStringContainerEntityValidatorTest {

    private ContainerValidatorStorage storage;

    @Before
    public void setUp() throws Exception {
        EntityStringFieldsCache cache = new EntityStringFieldsCache();
        AvoidEmptyStringContainerEntityValidator validator = new AvoidEmptyStringContainerEntityValidator(cache);
        storage = new ContainerValidatorStorage(Collections.singletonList(validator));
    }

    @Test
    public void validateCreate() {
        EntityValidator<TestEntity> v = storage.get(ContainerEntityValidator.AVOID_EMTPY_STRINGS);
        TestEntity entity = new TestEntity("test", 0);
        v.validateCreate(entity);
    }

    @Test(expected = BadRequestServiceException.class)
    public void validateCreateFailed() {
        EntityValidator<TestEntity> v = storage.get(ContainerEntityValidator.AVOID_EMTPY_STRINGS);
        TestEntity entity = new TestEntity("", 0);
        v.validateCreate(entity);
    }

    @Test
    public void validateUpdate() {
        EntityValidator<TestEntity> v = storage.get(ContainerEntityValidator.AVOID_EMTPY_STRINGS);
        TestEntity entity = new TestEntity("test", 0);
        v.validateUpdate(null, entity);
    }

    @Test(expected = BadRequestServiceException.class)
    public void validateUpdateFailed() {
        EntityValidator<TestEntity> v = storage.get(ContainerEntityValidator.AVOID_EMTPY_STRINGS);
        TestEntity entity = new TestEntity("", 0);
        v.validateUpdate(null, entity);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestEntity {
        @Column
        private String stringField;
        private Integer intField;
    }
}
