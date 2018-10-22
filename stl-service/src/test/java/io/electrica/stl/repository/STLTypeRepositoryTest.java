package io.electrica.stl.repository;

import io.electrica.stl.model.STLType;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class STLTypeRepositoryTest extends AbstractDatabaseTest {

    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateSTLWithSameName() {
//        setup
        stlTypeRepository.saveAndFlush(
                new STLType("Test")
        );

//        method
        stlTypeRepository.saveAndFlush(
                new STLType("Test")
        );
    }
}
