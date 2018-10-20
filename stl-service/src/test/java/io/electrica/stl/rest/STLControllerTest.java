package io.electrica.stl.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.test.context.ForUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class STLControllerTest extends AbstractDatabaseTest {

    @Inject
    private STLController stlController;

    private STLType stlType;

    @Before
    public void setup() {
        stlType = stlTypeRepository.findAll()
                .stream()
                .filter(st -> st.getName().equals("Foundation"))
                .findAny()
                .orElse(null);
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateSTLWithSuperAdminRole() {
        final CreateSTLDto dto = createHackerRankDto();
        final ReadSTLDto actual = stlController.create(dto).getBody();

        Assert.assertNotNull(actual.getId());
        Assert.assertNotNull(actual.getRevisionVersion());

        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getNamespace(), actual.getNamespace());
        assertEquals(dto.getVersion(), actual.getVersion());
        assertEquals(dto.getResource(), actual.getResource());

        Assert.assertEquals(stlType.getId(), actual.getTypeId());

        final String expectedErn = "stl://hackerrank:applications:1_0";
        assertEquals(expectedErn, actual.getErn());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateSTLWithNonExistingSTLType() {
        final CreateSTLDto dto = createHackerRankDto();
        dto.setTypeId(-1L);
        stlController.create(dto).getBody();
        stlRepository.flush();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateOrg
    )
    public void testCreateSTLWithWrongPermission() {
        stlController.create(createHackerRankDto());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateSTLWithWrongRole() {
        stlController.create(createHackerRankDto());
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testFindAll() {
        stlController.create(createHackerRankDto());

        final List<ReadSTLDto> actual = stlController.findAll().getBody();

        assertEquals(1, actual.size());
    }

    public CreateSTLDto createHackerRankDto() {
        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName("HackerRank");
        dto.setResource("Applications");
        dto.setNamespace("com.hackerrank");
        dto.setTypeId(stlType.getId());
        dto.setVersion("1.0");
        return dto;
    }
}
