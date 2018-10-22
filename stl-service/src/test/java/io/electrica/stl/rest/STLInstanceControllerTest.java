package io.electrica.stl.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STLInstance;
import io.electrica.stl.model.STLType;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.CreateSTLInstanceDto;
import io.electrica.stl.rest.dto.ReadSTLInstanceDto;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class STLInstanceControllerTest extends AbstractDatabaseTest {

    @Inject
    private STLController stlController;

    @Inject
    private STLInstanceController stlInstanceController;

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
            permissions = {
                    PermissionType.CreateSTL,
                    PermissionType.AssociateAccessKeyToSTL
            }
    )
    public void testCreateWithSuccess() {
        final Long stlId = stlController
                .create(createHackerRankSTLDto(stlType.getId(), authorizationType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateSTLInstanceDto dto = new CreateSTLInstanceDto(stlId, accessKeyId);
        final ReadSTLInstanceDto actual = stlInstanceController.create(dto).getBody();

        final STLInstance stlInstance = stlInstanceRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());
        assertNotNull(actual.getRevisionVersion());

        assertEquals(stlId, stlInstance.getStl().getId());
        assertEquals(accessKeyId, stlInstance.getAccessKeyId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.CreateSTL,
                    PermissionType.AssociateAccessKeyToSTL
            }
    )
    public void testCreateWithSameSTLAndAccessKey() {
        final Long stlId = stlController
                .create(createHackerRankSTLDto(stlType.getId(), authorizationType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateSTLInstanceDto dto = new CreateSTLInstanceDto(stlId, accessKeyId);

        stlInstanceController.create(dto);

        // assert
        stlInstanceController.create(dto);

        stlInstanceRepository.flush();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgUser,
            permissions = PermissionType.CreateSTL
    )
    public void testCreateWithWrongPermissions() {
        final Long stlId = stlController
                .create(createHackerRankSTLDto(stlType.getId(), authorizationType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateSTLInstanceDto dto = new CreateSTLInstanceDto(stlId, accessKeyId);
        stlInstanceController.create(dto);
    }
}
