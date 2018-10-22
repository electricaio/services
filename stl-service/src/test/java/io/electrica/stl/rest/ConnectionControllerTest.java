package io.electrica.stl.rest;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.Connection;
import io.electrica.stl.model.ConnectorType;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.ConnectDto;
import io.electrica.stl.rest.dto.ConnectionDto;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorController connectorController;

    @Inject
    private ConnectionController connectionController;

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
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector
            }
    )
    public void testConnectWithSuccess() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final ConnectDto dto = new ConnectDto(connectorId, accessKeyId);
        final ConnectionDto actual = connectionController.connect(dto).getBody();

        final Connection connection = connectionRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());

        assertEquals(connectorId, connection.getConnector().getId());
        assertEquals(accessKeyId, connection.getAccessKeyId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector
            }
    )
    public void testConnectWithSameConnectorAndAccessKey() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final ConnectDto dto = new ConnectDto(connectorId, accessKeyId);
        connectionController.connect(dto);

        // assert
        connectionController.connect(dto);

        connectionRepository.flush();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgUser,
            permissions = PermissionType.CreateConnector
    )
    public void testConnectWithWrongPermissions() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto(connectorType.getId(), authorizationType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final ConnectDto dto = new ConnectDto(connectorId, accessKeyId);
        connectionController.connect(dto);
    }
}
