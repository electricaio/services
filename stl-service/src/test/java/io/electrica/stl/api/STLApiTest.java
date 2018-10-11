package io.electrica.stl.api;

import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.ERNService;
import io.electrica.stl.service.STLService;
import org.junit.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import java.util.List;

import static org.junit.Assert.*;

public class STLApiTest extends AbstractDatabaseTest {

    @Inject
    private STLApi stlApi;

    @Inject
    private ERNService ernService;

    @Test
    public void testCreateSTLWithSuccess() {
        //        setup
        final String type = "Foundation";
        createSTLType(type);

        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName("HackerRank Applications");
        dto.setNamespace("api.hackerrank");
        dto.setType(type);
        dto.setVersion("1.0");

//        method
        final ReadSTLDto expected = stlApi.create(dto);

//        assert
        assertNotNull(expected.getId());

        assertEquals(expected.getErn(), dto.getName());
        assertEquals(expected.getName(), dto.getName());
        assertEquals(expected.getNamespace(), dto.getNamespace());
        assertEquals(expected.getType(), type);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCreateSTLWithAlreadyExistingName() {
        //        setup
        final String type = "Foundation";
        createSTLType(type);

        final String name = "HackerRank Applications";
        final String namespace = "api.hackerrank";
        final String version = "1.0";
        final String ern = ernService.assignERN(name);
        final STL stl = new STL();
        stl.setName(name);
        stl.setNamespace(namespace);
        stl.setVersion(version);
        stl.setErn(ern);

        stlRepository.saveAndFlush(stl);

        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName(name);
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
        stlRepository.saveAndFlush(greenhouseSTL);

//        method
        final List<ReadSTLDto> result = stlApi.findAll();

//        assert
        assertEquals(2, result.size());
    }
}
