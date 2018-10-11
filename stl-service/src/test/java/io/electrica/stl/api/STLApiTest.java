package io.electrica.stl.api;

import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.util.ERNUtils;
import org.junit.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class STLApiTest extends AbstractDatabaseTest {

    @Inject
    private STLApi stlApi;

    @Test
    public void testCreateSTLWithSuccess() {
        //        setup
        final String type = "Foundation";
        createSTLType(type);

        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName("HackerRank");
        dto.setResource("Applications");
        dto.setNamespace("com.hackerrank");
        dto.setType(type);
        dto.setVersion("1.0");

//        method
        final ReadSTLDto actual = stlApi.create(dto);

//        assert
        assertNotNull(actual.getId());

        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getNamespace(), actual.getNamespace());
        assertEquals(type, actual.getType());
        final String expectedErn = "stl://hackerrank:applications:1.0";
        assertEquals(expectedErn, actual.getErn());
        assertNotNull(actual.getRevisionVersion());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCreateSTLWithAlreadyExistingName() {
        //        setup
        final String type = "Foundation";
        createSTLType(type);

        final String name = "HackerRank";
        final String resource = "Applications";
        final String namespace = "com.hackerrank";
        final String version = "1.0";
        final String ern = ERNUtils.createERN(name, Optional.of(resource), version);
        final STL stl = new STL();
        stl.setName(name);
        stl.setNamespace(namespace);
        stl.setVersion(version);
        stl.setErn(ern);

        stlRepository.saveAndFlush(stl);

        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName(name);
        dto.setResource(resource);
        dto.setNamespace(namespace);
        dto.setType(type);
        dto.setVersion(version);

//        method
        stlApi.create(dto);
    }

    @Test
    public void testListExistingSTLs() {
        final STLType type = createSTLType("Foundation");

        final STL hackerRankSTL = new STL();
        final String hackerRankName = "HackerRank";
        final String hackerRankResource = "Applications";
        final String hackerRankVersion = "1.0";
        hackerRankSTL.setName(hackerRankName);
        hackerRankSTL.setNamespace("api.hackerrank");
        hackerRankSTL.setResource(hackerRankResource);
        hackerRankSTL.setVersion(hackerRankVersion);
        hackerRankSTL.setErn(ERNUtils.createERN(hackerRankName, Optional.of(hackerRankResource), hackerRankVersion));
        hackerRankSTL.setType(type);
        stlRepository.saveAndFlush(hackerRankSTL);

        final STL greenhouseSTL = new STL();
        final String greenhouseName = "Greenhouse";
        final String greenhouseVersion = "1.1";
        greenhouseSTL.setName(greenhouseName);
        greenhouseSTL.setNamespace("api.greenhouse");
        greenhouseSTL.setVersion(greenhouseVersion);
        greenhouseSTL.setErn(ERNUtils.createERN(greenhouseName, greenhouseVersion));
        greenhouseSTL.setType(type);
        stlRepository.saveAndFlush(greenhouseSTL);

//        method
        final List<ReadSTLDto> result = stlApi.findAll();

//        assert
        assertEquals(2, result.size());
    }
}
