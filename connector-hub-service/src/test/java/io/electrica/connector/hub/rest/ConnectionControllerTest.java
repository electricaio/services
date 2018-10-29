package io.electrica.connector.hub.rest;

import com.google.common.collect.Sets;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.model.*;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import io.electrica.connector.hub.rest.dto.*;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.CreateConnector,
            PermissionType.AssociateAccessKeyToConnector
    })
    public void testConnectWithSuccess() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final ConnectDto dto = new ConnectDto(connectorId, accessKeyId);
        final ConnectionDto actual = connectionController.connect(dto).getBody();

        final Connection connection = connectionRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());

        assertEquals(connectorId, connection.getConnector().getId());
        assertEquals(Long.valueOf(1), connection.getUserId());
        assertEquals(Long.valueOf(1), connection.getOrganizationId());
        assertEquals(accessKeyId, connection.getAccessKeyId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {PermissionType.CreateConnector,
            PermissionType.AssociateAccessKeyToConnector})
    public void testConnectWithSameConnectorAndAccessKey() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
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
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.CreateConnector)
    public void testConnectWithWrongPermissions() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
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
        final String tokenName = "token_name" + connectorId;
        request.setUser(user);
        request.setPassword(password);
        request.setName(tokenName);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        final ReadAuthorizationDto result = connectionController.authorizeWithUserAndPassword(
                connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final Authorization authorization = authorizationRepository.findById(result.getId()).orElse(null);
        assertEquals(connectorId, authorization.getConnection().getId());
        assertEquals(basicAuthType.getId(), authorization.getType().getId());
        assertEquals(tokenName, authorization.getName());
        assertNull(authorization.getTenantRefId());

        final BasicAuthorization basicAuthorization = basicAuthorizationRepository.findAll().get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertEquals(user, basicAuthorization.getUserHash());
        assertEquals(password, basicAuthorization.getPasswordHash());
    }


    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.CreateConnector)
    public void testBasicAuthWithWrongPermission() {
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto("user", "password");
        connectionController.authorizeWithUserAndPassword(1L, dto).getBody();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgAdmin, permissions = {
            PermissionType.CreateConnector,
            PermissionType.AssociateAccessKeyToConnector
    })
    public void testBasicAuthWithNonExistingConnection() {
        final Long connectionId = -1L;

        final CreateBasicAuthorizationDto request = new CreateBasicAuthorizationDto(
                "user_" + connectionId, "pwd_" + connectionId
        );
        connectionController.authorizeWithUserAndPassword(connectionId, request);
    }

    @Test(expected = AccessDeniedException.class)
    public void testBasicAuthWithConnectionFromDiffUser() {

        final AtomicLong wrapper = new AtomicLong();

        executeForUser(-1L, -1L, Sets.newHashSet(RoleType.SuperAdmin), Sets.newHashSet(
                PermissionType.CreateConnector,
                PermissionType.AssociateAccessKeyToConnector),
                () -> {
                    final Long connectionId = createConnectionToHackerRank();
                    wrapper.set(connectionId);
                });

        executeForUser(1L, 1L, Sets.newHashSet(RoleType.OrgAdmin), Sets.newHashSet(
                PermissionType.CreateConnector,
                PermissionType.AssociateAccessKeyToConnector),
                () -> {

                    final Long connectionId = wrapper.get();
                    final CreateBasicAuthorizationDto request = new CreateBasicAuthorizationDto(
                            "user_" + connectionId, "pwd_" + connectionId
                    );
                    connectionController.authorizeWithUserAndPassword(connectionId, request);
                });
    }

    /**
     * Tests whether if a user provides new credentials to already authorized connection,
     * it sets new credentials in
     * the current authorization entities.
     */
    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.AssociateAccessKeyToConnector, PermissionType.CreateConnector})
    public void testBasicAuthWithAlreadyExistingAuth() {
        final Long connectionId = createConnectionToHackerRank();

        final CreateBasicAuthorizationDto request = new CreateBasicAuthorizationDto();
        final String user = "user_" + connectionId;
        final String password = "pwd_" + connectionId;
        final String tokenName = "token_name" + connectionId;
        request.setUser(user);
        request.setPassword(password);
        request.setName(tokenName);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        connectionController.authorizeWithUserAndPassword(connectionId, request);

        request.setUser("new_user_" + connectionId);
        request.setPassword("new_pwd_" + connectionId);

        final ReadAuthorizationDto result = connectionController.authorizeWithUserAndPassword(
                connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(1, authorizations.size());
        final Authorization authorization = authorizations.get(0);
        assertEquals(result.getId(), authorization.getId());
        assertEquals(connectionId, authorization.getConnection().getId());
        assertEquals(basicAuthType.getId(), authorization.getType().getId());
        assertEquals(tokenName, authorization.getName());
        assertNull(authorization.getTenantRefId());

        final List<BasicAuthorization> basicAuthorizations = basicAuthorizationRepository.findAll();
        assertEquals(1, basicAuthorizations.size());
        final BasicAuthorization basicAuthorization = basicAuthorizations.get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertEquals(user, basicAuthorization.getUserHash());
        assertEquals(password, basicAuthorization.getPasswordHash());
    }


    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.AssociateAccessKeyToConnector, PermissionType.CreateConnector})
    public void testTokenAuthWithSuccess() {
        final Long connectorId = createConnectionToHackerRank();
        final CreateTokenAuthorizationDto request = new CreateTokenAuthorizationDto();
        final String token = "token" + connectorId;
        request.setToken(token);
        final String tokenName = "token_name" + connectorId;
        request.setName(tokenName);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(tokenAuthorizationRepository.findAll().isEmpty());

        final ReadAuthorizationDto result = connectionController.authorizeWithToken(
                connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final Authorization authorization = authorizationRepository.findById(result.getId()).orElse(null);
        assertEquals(connectorId, authorization.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization.getType().getId());
        assertEquals(tokenName, authorization.getName());
        assertNull(authorization.getTenantRefId());

        final TokenAuthorization basicAuthorization = tokenAuthorizationRepository.findAll().get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertEquals(token, basicAuthorization.getToken());
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.AssociateAccessKeyToConnector, PermissionType.CreateConnector})
    public void testTokenAuthWith2Authorizations() {
        final Long connectorId = createConnectionToHackerRank();
        final CreateTokenAuthorizationDto request = new CreateTokenAuthorizationDto();
        final String token = "token" + connectorId;
        request.setToken(token);
        final String tokenName = "token_name" + connectorId;
        request.setName(tokenName);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(tokenAuthorizationRepository.findAll().isEmpty());

        connectionController.authorizeWithToken(connectorId, request);

        final String otherToken = "other_token" + connectorId;
        request.setToken(otherToken);
        final String otherTokenName = "other_token_name" + connectorId;
        request.setName(otherTokenName);

        final ReadAuthorizationDto result = connectionController.authorizeWithToken(connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(2, authorizations.size());

        // auth 1
        final Authorization authorization1 = authorizations.get(0);
        assertEquals(connectorId, authorization1.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization1.getType().getId());
        assertEquals(tokenName, authorization1.getName());
        assertNull(authorization1.getTenantRefId());

        final List<TokenAuthorization> tokenAuth = tokenAuthorizationRepository.findAll();

        final TokenAuthorization tokenAuth1 = tokenAuth.get(0);
        assertEquals(authorization1.getId(), tokenAuth1.getAuthorization().getId());
        assertEquals(token, tokenAuth1.getToken());

        // auth 2
        final Authorization authorization2 = authorizations.get(1);
        assertEquals(connectorId, authorization2.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization2.getType().getId());
        assertEquals(otherTokenName, authorization2.getName());
        assertNull(authorization2.getTenantRefId());

        final TokenAuthorization tokenAuth2 = tokenAuth.get(1);
        assertEquals(authorization2.getId(), tokenAuth2.getAuthorization().getId());
        assertEquals(otherToken, tokenAuth2.getToken());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.CreateConnector)
    public void testTokenAuthWithWrongPermission() {
        final CreateTokenAuthorizationDto dto = new CreateTokenAuthorizationDto();
        dto.setToken("token");
        connectionController.authorizeWithToken(1L, dto).getBody();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgAdmin, permissions = {
            PermissionType.CreateConnector,
            PermissionType.AssociateAccessKeyToConnector
    })
    public void testTokenAuthWithNonExistingConnection() {
        final Long connectionId = -1L;
        final CreateTokenAuthorizationDto request = new CreateTokenAuthorizationDto();
        request.setToken("token");
        connectionController.authorizeWithToken(connectionId, request);
    }

    @Test(expected = AccessDeniedException.class)
    public void testTokenAuthWithConnectionFromDiffUser() {

        final AtomicLong wrapper = new AtomicLong();

        executeForUser(-1L, -1L, Sets.newHashSet(RoleType.SuperAdmin), Sets.newHashSet(
                PermissionType.CreateConnector,
                PermissionType.AssociateAccessKeyToConnector),
                () -> {
                    final Long connectionId = createConnectionToHackerRank();
                    wrapper.set(connectionId);
                });

        executeForUser(1L, 1L, Sets.newHashSet(RoleType.OrgAdmin), Sets.newHashSet(
                PermissionType.CreateConnector,
                PermissionType.AssociateAccessKeyToConnector), () -> {
            final Long connectionId = wrapper.get();
            final CreateTokenAuthorizationDto request = new CreateTokenAuthorizationDto();
            request.setToken("token");
            connectionController.authorizeWithToken(connectionId, request);
        });
    }

    /**
     * Tests whether if a user provides new credentials to already authorized connection,
     * it sets new credentials in
     * the current authorization entities.
     */
    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.AssociateAccessKeyToConnector, PermissionType.CreateConnector})
    public void testTokenAuthWithAlreadyExistingAuth() {
        final Long connectionId = createConnectionToHackerRank();

        final CreateTokenAuthorizationDto request = new CreateTokenAuthorizationDto();
        final String token = "token" + connectionId;
        request.setToken(token);
        final String tokenName = "token_name" + connectionId;
        request.setName(tokenName);

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        connectionController.authorizeWithToken(connectionId, request);

        request.setToken("newtoken_" + connectionId);

        final ReadAuthorizationDto result = connectionController.authorizeWithToken(connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(1, authorizations.size());
        final Authorization authorization = authorizations.get(0);
        assertEquals(result.getId(), authorization.getId());
        assertEquals(connectionId, authorization.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization.getType().getId());
        assertEquals(tokenName, authorization.getName());
        assertNull(authorization.getTenantRefId());

        final List<TokenAuthorization> tokenAuthorizations = tokenAuthorizationRepository.findAll();
        assertEquals(1, tokenAuthorizations.size());
        final TokenAuthorization tokenAuthorization = tokenAuthorizations.get(0);
        assertEquals(authorization.getId(), tokenAuthorization.getAuthorization().getId());
        assertEquals(token, tokenAuthorization.getToken());
    }


    private Long createConnectionToHackerRank() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final ConnectDto dto = new ConnectDto();
        dto.setConnectorId(connectorId);
        dto.setAccessKeyId(-1L);
        return connectionController.connect(dto).getBody().getId();
    }

    /**
     * It creates 3 connectors as super admin.
     * - HackerRank, Greenhouse and MySQL.
     * <p>
     * For user with id: 1, it creates:
     * - 2 HackerRank connections - keys 1,2
     * - 1 Greenhouse connection - keys: 1
     * <p>
     * For user with id: 2, it creates:
     * - 1 Greenhouse connection - keys: 3
     */
    @Test
    public void testFindAllByUser() {

        final AtomicReference<Long> cnHackerRank =
                new AtomicReference<>();
        final AtomicReference<Long> cnGreenhouse =
                new AtomicReference<>();
        final AtomicReference<Long> cnMySQL =
                new AtomicReference<>();

        // create connectors as super admin
        executeForUser(-1, -1,
                Sets.newHashSet(RoleType.SuperAdmin),
                Sets.newHashSet(PermissionType.CreateConnector),
                () -> {
                    cnHackerRank.set(connectorController
                            .create(createHackerRankConnectorDto())
                            .getBody().getId()
                    );
                    cnGreenhouse.set(connectorController
                            .create(createGreenhouseConnectorDto())
                            .getBody().getId()
                    );
                    cnMySQL.set(connectorController
                            .create(createMySQLConnectorDto())
                            .getBody().getId()
                    );
                }
        );

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        PermissionType.AssociateAccessKeyToConnector,
                        PermissionType.ReadActiveConnection), () -> {
                    connectionController.connect(new ConnectDto(cnGreenhouse.get(), 1L));
                    connectionController.connect(new ConnectDto(cnHackerRank.get(), 1L));
                    connectionController.connect(new ConnectDto(cnHackerRank.get(), 2L));
                });


        executeForUser(2, 2,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        PermissionType.AssociateAccessKeyToConnector,
                        PermissionType.ReadActiveConnection), () -> {
                    connectionController.connect(new ConnectDto(cnGreenhouse.get(), 3L));
                });

        flushAndClear();

        assertEquals(3, connectorRepository.findAll().size());

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(PermissionType.ReadActiveConnection), () -> {

                    final List<ConnectionDto> result = connectionController.findAllByUser(1L).getBody();
                    assertEquals(3, result.size());
                    assertEquals(cnGreenhouse.get(), result.get(0).getConnectorId());
                    assertEquals(Long.valueOf(1), result.get(0).getAccessKeyId());
                    assertEquals(cnHackerRank.get(), result.get(1).getConnectorId());
                    assertEquals(Long.valueOf(1), result.get(1).getAccessKeyId());
                    assertEquals(cnHackerRank.get(), result.get(2).getConnectorId());
                    assertEquals(Long.valueOf(2), result.get(2).getAccessKeyId());
                });

        executeForUser(2, 2,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(PermissionType.ReadActiveConnection), () -> {
                    final List<ConnectionDto> result = connectionController.findAllByUser(2L).getBody();
                    assertEquals(1, result.size());
                    assertEquals(cnGreenhouse.get(), result.get(0).getConnectorId());
                    assertEquals(Long.valueOf(3), result.get(0).getAccessKeyId());
                });


        executeForUser(3, 3,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(PermissionType.ReadActiveConnection), () -> {
                    final List<ConnectionDto> result = connectionController.findAllByUser(3L).getBody();
                    assertEquals(0, result.size());
                });
    }
}
