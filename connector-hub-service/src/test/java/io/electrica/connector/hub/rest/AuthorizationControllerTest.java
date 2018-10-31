package io.electrica.connector.hub.rest;

import com.google.common.collect.Sets;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.BasicAuthorization;
import io.electrica.connector.hub.model.TokenAuthorization;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class AuthorizationControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorControllerImpl connectorController;

    @Inject
    private ConnectionControllerImpl connectionController;

    @Inject
    private AuthorizationControllerImpl authorizationController;

    private AuthorizationType basicAuthType;
    private AuthorizationType tokenAuthType;

    @Before
    public void setup() {
        basicAuthType = findAuthorizationType(AuthorizationTypeName.BASIC_AUTHORIZATION);
        tokenAuthType = findAuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION);
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
        final CreateBasicAuthorizationDto request = createCreateBasicAuthorizationDto();

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        final ReadAuthorizationDto result = authorizationController.authorizeWithUserAndPassword(
                connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final Authorization authorization = authorizationRepository.findById(result.getId()).orElse(null);
        assertEquals(connectorId, authorization.getConnection().getId());
        assertEquals(basicAuthType.getId(), authorization.getType().getId());
        assertTrue(authorization.getName().startsWith("token_name_"));
        assertNull(authorization.getTenantRefId());

        final BasicAuthorization basicAuthorization = basicAuthorizationRepository.findAll().get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertTrue(basicAuthorization.getUserHash().startsWith("user_"));
        assertTrue(basicAuthorization.getPasswordHash().startsWith("pwd_"));
    }


    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.CreateConnector)
    public void testBasicAuthWithWrongPermission() {
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto("user", "password");
        authorizationController.authorizeWithUserAndPassword(1L, dto).getBody();
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
        authorizationController.authorizeWithUserAndPassword(connectionId, request);
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
                    authorizationController.authorizeWithUserAndPassword(connectionId, request);
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

        final CreateBasicAuthorizationDto request = createCreateBasicAuthorizationDto();

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        authorizationController.authorizeWithUserAndPassword(connectionId, request);

        request.setUser("new_user_" + connectionId);
        request.setPassword("new_pwd_" + connectionId);

        final ReadAuthorizationDto result = authorizationController.authorizeWithUserAndPassword(
                connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(1, authorizations.size());
        final Authorization authorization = authorizations.get(0);
        assertEquals(result.getId(), authorization.getId());
        assertEquals(connectionId, authorization.getConnection().getId());
        assertEquals(basicAuthType.getId(), authorization.getType().getId());
        assertTrue(authorization.getName().startsWith("token_name_"));
        assertNull(authorization.getTenantRefId());

        final List<BasicAuthorization> basicAuthorizations = basicAuthorizationRepository.findAll();
        assertEquals(1, basicAuthorizations.size());
        final BasicAuthorization basicAuthorization = basicAuthorizations.get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertTrue(basicAuthorization.getUserHash().startsWith("new_user_"));
        assertTrue(basicAuthorization.getPasswordHash().startsWith("new_pwd_"));
    }


    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.AssociateAccessKeyToConnector, PermissionType.CreateConnector})
    public void testTokenAuthWithSuccess() {
        final Long connectorId = createConnectionToHackerRank();
        final CreateTokenAuthorizationDto request = createCreateTokenAuthorizationDto();

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(tokenAuthorizationRepository.findAll().isEmpty());

        final ReadAuthorizationDto result = authorizationController.authorizeWithToken(
                connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final Authorization authorization = authorizationRepository.findById(result.getId()).orElse(null);
        assertEquals(connectorId, authorization.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization.getType().getId());
        assertTrue(authorization.getName().startsWith("token_name_"));
        assertNull(authorization.getTenantRefId());

        final TokenAuthorization basicAuthorization = tokenAuthorizationRepository.findAll().get(0);
        assertEquals(authorization.getId(), basicAuthorization.getAuthorization().getId());
        assertTrue(basicAuthorization.getToken().startsWith("token_"));
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.AssociateAccessKeyToConnector, PermissionType.CreateConnector})
    public void testTokenAuthWith2Authorizations() {
        final Long connectorId = createConnectionToHackerRank();
        final CreateTokenAuthorizationDto request = createCreateTokenAuthorizationDto();

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(tokenAuthorizationRepository.findAll().isEmpty());

        authorizationController.authorizeWithToken(connectorId, request);

        final String otherToken = "other_token" + connectorId;
        request.setToken(otherToken);
        final String otherTokenName = "other_token_name" + connectorId;
        request.setName(otherTokenName);

        final ReadAuthorizationDto result = authorizationController.authorizeWithToken(connectorId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(2, authorizations.size());

        // auth 1
        final Authorization authorization1 = authorizations.get(0);
        assertEquals(connectorId, authorization1.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization1.getType().getId());
        assertTrue(authorization1.getName().startsWith("token_name_"));
        assertNull(authorization1.getTenantRefId());

        final List<TokenAuthorization> tokenAuth = tokenAuthorizationRepository.findAll();

        final TokenAuthorization tokenAuth1 = tokenAuth.get(0);
        assertEquals(authorization1.getId(), tokenAuth1.getAuthorization().getId());
        assertTrue(tokenAuth1.getToken().startsWith("token_"));

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
        authorizationController.authorizeWithToken(1L, dto).getBody();
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
        authorizationController.authorizeWithToken(connectionId, request);
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
            authorizationController.authorizeWithToken(connectionId, request);
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
        final CreateTokenAuthorizationDto request = createCreateTokenAuthorizationDto();

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        authorizationController.authorizeWithToken(connectionId, request);

        request.setToken("new_token_" + connectionId);

        final ReadAuthorizationDto result = authorizationController.authorizeWithToken(connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());

        final List<Authorization> authorizations = authorizationRepository.findAll();
        assertEquals(1, authorizations.size());
        final Authorization authorization = authorizations.get(0);
        assertEquals(result.getId(), authorization.getId());
        assertEquals(connectionId, authorization.getConnection().getId());
        assertEquals(tokenAuthType.getId(), authorization.getType().getId());
        assertTrue(authorization.getName().startsWith("token_name_"));
        assertNull(authorization.getTenantRefId());

        final List<TokenAuthorization> tokenAuthorizations = tokenAuthorizationRepository.findAll();
        assertEquals(1, tokenAuthorizations.size());
        final TokenAuthorization tokenAuthorization = tokenAuthorizations.get(0);
        assertEquals(authorization.getId(), tokenAuthorization.getAuthorization().getId());
        assertTrue(tokenAuthorization.getToken().startsWith("new_token_"));
    }

    @Test
    @ForUser(userId = 1, organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.AssociateAccessKeyToConnector,
                    PermissionType.CreateConnector,
                    PermissionType.ReadActiveConnection}
    )
    public void testFindAllByConnectionId() {
        final Long connectionId = createConnectionToHackerRank();
        final CreateTokenAuthorizationDto request = createCreateTokenAuthorizationDto();

        assertTrue(authorizationRepository.findAll().isEmpty());
        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        authorizationController.authorizeWithToken(connectionId, request);

        request.setName("new_token_name_" + connectionId);
        request.setToken("new_token_" + connectionId);
        authorizationController.authorizeWithToken(connectionId, request).getBody();

        final List<ReadAuthorizationDto> result = authorizationController.findAll(connectionId).getBody();

        assertEquals(2, result.size());
        assertTrue(result.get(0).getName().startsWith("token_name_"));
        assertTrue(result.get(1).getName().startsWith("new_token_name_"));
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

    private CreateBasicAuthorizationDto createCreateBasicAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto();
        final String user = "user_" + randomUUID;
        final String password = "pwd_" + randomUUID;
        final String tokenName = "token_name_" + randomUUID;
        dto.setUser(user);
        dto.setPassword(password);
        dto.setName(tokenName);
        return dto;
    }

    private CreateTokenAuthorizationDto createCreateTokenAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateTokenAuthorizationDto dto = new CreateTokenAuthorizationDto();
        final String token = "token_" + randomUUID;
        dto.setToken(token);
        final String tokenName = "token_name_" + randomUUID;
        dto.setName(tokenName);
        return dto;
    }
}
