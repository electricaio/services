package io.electrica.stl.repository;

import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.util.ERNUtils;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class STLRepositoryTest extends AbstractDatabaseTest {


    /**
     * Tests a case when resource is not provided and adding
     * 2 STLs with same name should raise constraint exception on ERN,
     * since they would end up being same.
     * */
    @Test(expected = DataIntegrityViolationException.class)
    public void testSaveSTLWithoutResourceResultingInSameERN() {

        final STLType type = createSTLType("Foundation");

        final String name = "MySQL";
        final String namespace = "com.mysql";
        final String version = "5.6";
        final String ern = ERNUtils.createERN(name, version);

        final STL first = new STL();
        first.setType(type);
        first.setName(name);
        first.setVersion(version);
        first.setNamespace(namespace);
        first.setErn(ern);
        stlRepository.saveAndFlush(first);

        final STL second = new STL();
        second.setType(type);
        second.setName(name);
        second.setVersion(version);
        second.setNamespace(namespace);
        second.setErn(ern);
        stlRepository.saveAndFlush(second);
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
        final String hackerRankName = "HackerRank";
        final String hackerRankVersion = "1.0";
        final String hackerRankResource = "Applications";
        hackerRankSTL.setName(hackerRankName);
        hackerRankSTL.setNamespace("api.hackerrank");
        hackerRankSTL.setVersion("1.0");
        hackerRankSTL.setErn(ERNUtils.createERN(hackerRankName, Optional.of(hackerRankResource), hackerRankVersion));
        hackerRankSTL.setType(type);
        stlRepository.saveAndFlush(hackerRankSTL);

        final STL greenhouseSTL = new STL();
        final String greenhouseName = "Greenhouse";
        final String greenHouseVersion = "1.1";
        greenhouseSTL.setName(greenhouseName);
        greenhouseSTL.setNamespace("api.greenhouse");
        greenhouseSTL.setVersion("1.1");
        greenhouseSTL.setErn(ERNUtils.createERN(greenhouseName, greenHouseVersion));
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
