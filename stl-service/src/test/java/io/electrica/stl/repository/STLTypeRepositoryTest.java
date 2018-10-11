package io.electrica.stl.repository;

import io.electrica.stl.model.STLType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class STLTypeRepositoryTest extends AbstractDatabaseTest {

    @Test
    public void testCreateSTLTypeWithSuccess() {
//        setup
        final STLType type = new STLType();
        type.setName("Foundation");

//        method
        stlTypeRepository.save(type);

//        assert
        final List<STLType> result = stlTypeRepository.findAll();

        assertEquals(1, result.size());

        final STLType actual = result.get(0);
        assertEquals("Foundation", actual.getName());
    }
}
