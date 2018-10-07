package io.electrica.stl.repository;

import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class STLRepositoryTest extends AbstractDatabaseTest {

    @Test
    public void test_create_stl_with_success() {
//        setup
        final STLType type = new STLType();
        type.setName("Foundation");

        stlTypeRepository.save(type);

        final String name = "Hackerrank API";
        final String namespace = "stl.hackerrank";
        final String version = "0.0.1";
        final String ern = "stl://hackerrank:v0.0.1";

        final STL stl = new STL();
        stl.setName(name);
        stl.setNamespace(namespace);
        stl.setVersion(version);
        stl.setErn(ern);
        stl.setType(type);

//        method
        stlRepository.save(stl);

//        assert
        final List<STL> result = stlRepository.findAll();

        assertEquals(1, result.size());

        final STL actual = result.get(0);
        assertEquals(actual.getName(), name);
        assertEquals(actual.getNamespace(), namespace);
        assertEquals(actual.getErn(), ern);
        assertEquals(actual.getName(), name);
        assertEquals(actual.getType().getId(), type.getId());
    }
}
