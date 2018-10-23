package io.electrica.connector.hub.rest;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.model.*;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

public class ConnectionControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorController connectorController;

    @Inject
    private ConnectionController connectionController;

    private ConnectorType connectorType;

    private AuthorizationType tokenAuthType;

    private AuthorizationType basicAuthType;

    @Before
    public void setup() {
        super.setup();
        connectorType = findConnectorType("Talent");
        tokenAuthType = findAuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION);
        basicAuthType = findAuthorizationType(AuthorizationTypeName.BASIC_AUTHORIZATION);
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
                .create(createHackerRankConnectorDto(connectorType.getId(), tokenAuthType.getId()))
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
                .create(createHackerRankConnectorDto(connectorType.getId(), tokenAuthType.getId()))
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
                .create(createHackerRankConnectorDto(connectorType.getId(), tokenAuthType.getId()))
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final ConnectDto dto = new ConnectDto(connectorId, accessKeyId);
        connectionController.connect(dto);
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
    public void testBasicAuthWithSuccess() {
        final Long connectorId = createConnectionToHackerRank();
        final CreateBasicAuthorizationDto request = new CreateBasicAuthorizationDto();
        final String user = "user_" + connectorId;
        final String password = "pwd_" + connectorId;
        request.setUser(user);
        request.setPassword(password);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        final ReadAuthorizationDto result = connectionController.authorize(connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final Authorization authorization = authorizationRepository.findById(result.getId()).orElse(null);
        assertEquals(connectorId, authorization.getConnection().getId());
        assertEquals(basicAuthType.getId(), authorization.getType().getId());

        final BasicAuthorization basicAuthorization = basicAuthorizationRepository.findAll().get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertEquals(user, basicAuthorization.getUserHash());
        assertEquals(password, basicAuthorization.getPasswordHash());
    }


    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgUser,
            permissions = PermissionType.CreateConnector
    )
    public void testBasicAuthWithWrongPermission() {
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto("user", "password");
        connectionController.authorize(1L, dto).getBody();
    }

    @Test(expected = EntityNotFoundServiceException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgAdmin,
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector
            }
    )
    public void testBasicAuthWithNonExistingConnection() {
        final Long connectionId = -1L;

        final CreateBasicAuthorizationDto request = new CreateBasicAuthorizationDto(
                "user_" + connectionId, "pwd_" + connectionId
        );
        connectionController.authorize(connectionId, request);
    }

    /**
     * Tests whether if a user provides new credentials to already authorized connection,
     * it sets new credentials in
     * the current authorization entities.
     */
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
    public void testBasicAuthWithAlreadyExistingAuth() {
        final Long connectionId = createConnectionToHackerRank();

        final CreateBasicAuthorizationDto request = new CreateBasicAuthorizationDto();
        final String user = "user_" + connectionId;
        final String password = "pwd_" + connectionId;
        request.setUser(user);
        request.setPassword(password);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        connectionController.authorize(connectionId, request);

        request.setUser("new_user_" + connectionId);
        request.setPassword("new_pwd_" + connectionId);

        final ReadAuthorizationDto result = connectionController.authorize(connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(1, authorizations.size());
        final Authorization authorization = authorizations.get(0);
        assertEquals(result.getId(), authorization.getId());
        assertEquals(connectionId, authorization.getConnection().getId());
        assertEquals(basicAuthType.getId(), authorization.getType().getId());

        final List<BasicAuthorization> basicAuthorizations = basicAuthorizationRepository.findAll();
        assertEquals(1, basicAuthorizations.size());
        final BasicAuthorization basicAuthorization = basicAuthorizations.get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertEquals(user, basicAuthorization.getUserHash());
        assertEquals(password, basicAuthorization.getPasswordHash());
    }

    private Long createConnectionToHackerRank() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto(connectorType.getId(), tokenAuthType.getId()))
                .getBody()
                .getId();

        final ConnectDto dto = new ConnectDto();
        dto.setConnectorId(connectorId);
        dto.setAccessKeyId(-1L);
        return connectionController.connect(dto).getBody().getId();
    }
}
