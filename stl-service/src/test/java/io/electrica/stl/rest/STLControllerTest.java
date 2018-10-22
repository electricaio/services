package io.electrica.stl.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STLType;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.STLService;
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

    @Inject
    private STLService stlService;

    private STLType stlType;

    private AuthorizationType authorizationType;

    @Before
    public void setup() {
        super.setup();
        stlType = findSTLType("Talent");
        authorizationType = findAuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION);
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateSTLWithSuccess() {
        final CreateSTLDto dto = createHackerRankSTLDto(stlType.getId(), authorizationType.getId());
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
        final CreateSTLDto dto = createHackerRankSTLDto(stlType.getId(), authorizationType.getId());
        dto.setTypeId(-1L);
        stlController.create(dto).getBody();
        stlRepository.flush();
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateSTLWithNonExistingAuthType() {
        final CreateSTLDto dto = createHackerRankSTLDto(stlType.getId(), authorizationType.getId());
        dto.setAuthorizationTypeId(-1L);
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
        stlController.create(createHackerRankSTLDto(stlType.getId(), authorizationType.getId()));
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateSTLWithWrongRole() {
        stlController.create(createHackerRankSTLDto(stlType.getId(), authorizationType.getId()));
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateSTL
    )
    public void testFindAllNonArchived() {
        final CreateSTLDto createHackerRankSTLDto = createHackerRankSTLDto(stlType.getId(), authorizationType.getId());
        stlController.create(createHackerRankSTLDto);

        final ReadSTLDto greenHouseDto = stlController.create(createGreenhouseSTLDto(stlType.getId(), authorizationType
                .getId())).getBody();
        stlService.archive(greenHouseDto.getId());

        final List<ReadSTLDto> actual = stlController.findAll().getBody();

        assertEquals(1, actual.size());
        assertEquals(createHackerRankSTLDto.getName(), actual.get(0).getName());
    }
}
