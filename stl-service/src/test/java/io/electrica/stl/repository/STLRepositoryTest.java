package io.electrica.stl.repository;

import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.service.ERNService;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class STLRepositoryTest extends AbstractDatabaseTest {

    @Inject
    private ERNService ernService;

    @Test
    public void testCreateSTLWithSuccess() {
//        setup
        final STLType type = new STLType();
        type.setName("Foundation");

        stlTypeRepository.saveAndFlush(type);

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
        stlRepository.saveAndFlush(stl);

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

    @Test
    public void testFindAllNonArchived() {
//        setup
        final STLType type = new STLType();
        type.setName("Foundation");

        stlTypeRepository.saveAndFlush(type);

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

        final STL hackerRankSTL = new STL();
        final String hackerRankName = "HackerRank Applications";
        hackerRankSTL.setName(hackerRankName);
        hackerRankSTL.setNamespace("api.hackerrank");
        hackerRankSTL.setVersion("1.0");
        hackerRankSTL.setErn(ernService.assignERN(hackerRankName));
        hackerRankSTL.setType(type);
        stlRepository.saveAndFlush(hackerRankSTL);

        final STL greenhouseSTL = new STL();
        final String greenhouseName = "Greenhouse";
        greenhouseSTL.setName(greenhouseName);
        greenhouseSTL.setNamespace("api.greenhouse");
        greenhouseSTL.setVersion("1.1");
        greenhouseSTL.setErn(ernService.assignERN(greenhouseName));
        greenhouseSTL.setType(type);
        greenhouseSTL.setArchived(true);
        stlRepository.saveAndFlush(greenhouseSTL);

//        method
        final List<STL> result = stlRepository.findAllNonArchived();

//        assert
        assertEquals(1, result.size());

        assertEquals(hackerRankName, result.get(0).getName());
    }
}
