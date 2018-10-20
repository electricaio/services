package io.electrica.stl.repository;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class STLTypeRepositoryTest extends AbstractDatabaseTest {

    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateSTLWithSameName() {
//        setup
        final String name = "Test";
        createSTLType(name);

//        method
        createSTLType(name);
    }
}
