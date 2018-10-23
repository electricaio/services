package io.electrica.connector.hub.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import io.electrica.connector.hub.rest.dto.CreateConnectorDto;
import io.electrica.connector.hub.rest.dto.ReadConnectorDto;
import io.electrica.connector.hub.service.ConnectorService;
import io.electrica.test.context.ForUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConnectorControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorController connectorController;

    @Inject
    private ConnectorService connectorService;

    private ConnectorType connectorType;

    private AuthorizationType authorizationType;

    @Before
    public void setup() {
        super.setup();
        connectorType = findConnectorType("Talent");
        authorizationType = findAuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION);
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithSuccess() {
        final CreateConnectorDto dto = createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId());
        final ReadConnectorDto actual = connectorController.create(dto).getBody();

        Assert.assertNotNull(actual.getId());
        Assert.assertNotNull(actual.getRevisionVersion());

        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getNamespace(), actual.getNamespace());
        assertEquals(dto.getVersion(), actual.getVersion());
        assertEquals(dto.getResource(), actual.getResource());

        Assert.assertEquals(connectorType.getId(), actual.getTypeId());

        final String expectedErn = "ern://connector:hackerrank:applications:1_0";
        assertEquals(expectedErn, actual.getErn());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithNonExistingSTLType() {
        final CreateConnectorDto dto = createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId());
        dto.setTypeId(-1L);
        connectorController.create(dto).getBody();
        connectorRepository.flush();
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithNonExistingAuthType() {
        final CreateConnectorDto dto = createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId());
        dto.setAuthorizationTypeId(-1L);
        connectorController.create(dto).getBody();
        connectorRepository.flush();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateOrg
    )
    public void testCreateConnectorWithWrongPermission() {
        connectorController.create(createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId()));
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithWrongRole() {
        connectorController.create(createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId()));
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testFindAllNonArchived() {
        final CreateConnectorDto createHackerRankSTLDto = createHackerRankConnectorDto(
                connectorType.getId(),
                authorizationType.getId()
        );
        connectorController.create(createHackerRankSTLDto);

        final ReadConnectorDto greenHouseDto = connectorController
                .create(createGreenhouseConnectorDto(connectorType.getId(), authorizationType.getId()))
                .getBody();
        connectorService.archive(greenHouseDto.getId());

        final List<ReadConnectorDto> actual = connectorController.findAll().getBody();

        assertEquals(1, actual.size());
        assertEquals(createHackerRankSTLDto.getName(), actual.get(0).getName());
    }
}
