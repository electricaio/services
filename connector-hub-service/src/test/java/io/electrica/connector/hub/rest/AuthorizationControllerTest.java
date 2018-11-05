package io.electrica.connector.hub.rest;

import com.google.common.collect.Sets;
import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.*;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import io.electrica.test.context.ForUser;
import io.electrica.user.feign.AccessKeyClient;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class AuthorizationControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorControllerImpl connectorController;

    @Inject
    private ConnectionControllerImpl connectionController;

    @Inject
    private AuthorizationControllerImpl authorizationController;

    @MockBean
    @Inject
    private AccessKeyClient accessKeyClient;

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector,
                    PermissionType.ReadActiveConnection
            }
    )
    public void testBasicAuthWithSuccess() {
        Long connectionId = createConnectionToMySql();
        CreateBasicAuthorizationDto request = createCreateBasicAuthorizationDto();

        assertTrue(basicAuthorizationRepository.findAll().isEmpty());

        BasicAuthorizationDto result = authorizationController.authorizeWithBasic(connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());
        assertNull(result.getTenantRefId());

        ConnectionDto connection = connectionController.get(connectionId).getBody();
        assertEquals(connection.getAuthorizationId(), result.getId());

        assertTrue(result.getUsername().startsWith("user_"));
        assertTrue(result.getPassword().startsWith("pwd_"));

        BasicAuthorizationDto get = authorizationController.getBasic(result.getId()).getBody();
        assertTrue(get.getUsername().startsWith("user_"));
        assertTrue(get.getPassword().startsWith("pwd_"));


        get.setUsername("test");
        BasicAuthorizationDto update = authorizationController.updateBasic(get.getId(), get).getBody();
        assertEquals("test", update.getUsername());
        assertTrue(update.getPassword().startsWith("pwd_"));

    }

    @Test(expected = BadRequestServiceException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector
            }
    )
    public void testBasicAuthWithTokenConnector() {
        Long connectionId = createConnectionToHackerRank();
        CreateBasicAuthorizationDto request = createCreateBasicAuthorizationDto();

        authorizationController.authorizeWithBasic(connectionId, request).getBody();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = PermissionType.CreateConnector)
    public void testBasicAuthWithWrongPermission() {
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto("user", "password");
        authorizationController.authorizeWithBasic(1L, dto).getBody();
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
        authorizationController.authorizeWithBasic(connectionId, request);
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
                    authorizationController.authorizeWithBasic(connectionId, request);
                });
    }


    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector,
                    PermissionType.ReadActiveConnection
            }
    )
    public void testTokenAuthWithSuccess() {
        Long connectionId = createConnectionToHackerRank();
        CreateTokenAuthorizationDto request = createCreateTokenAuthorizationDto();

        assertTrue(tokenAuthorizationRepository.findAll().isEmpty());

        TokenAuthorizationDto result = authorizationController.authorizeWithToken(connectionId, request).getBody();

        assertNotNull(result.getId());
        assertNotNull(result.getRevisionVersion());
        assertNull(result.getTenantRefId());

        ConnectionDto connection = connectionController.get(connectionId).getBody();
        assertEquals(connection.getAuthorizationId(), result.getId());

        assertTrue(result.getToken().startsWith("token_"));

        TokenAuthorizationDto get = authorizationController.getToken(result.getId()).getBody();
        assertTrue(get.getToken().startsWith("token_"));

        get.setToken("test");
        TokenAuthorizationDto update = authorizationController.updateToken(get.getId(), get).getBody();
        assertEquals("test", update.getToken());
    }

    @Test(expected = BadRequestServiceException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = {
                    PermissionType.CreateConnector,
                    PermissionType.AssociateAccessKeyToConnector
            }
    )
    public void testTokenAuthWithBasicConnector() {
        Long connectionId = createConnectionToMySql();
        CreateTokenAuthorizationDto request = createCreateTokenAuthorizationDto();

        authorizationController.authorizeWithToken(connectionId, request).getBody();
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

    private Long createConnectionToMySql() {
        final Long connectorId = connectorController
                .create(createMySQLConnectorDto())
                .getBody()
                .getId();

        final CreateConnectionDto dto = new CreateConnectionDto();
        dto.setName("Default");
        dto.setConnectorId(connectorId);
        dto.setAccessKeyId(-1L);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(dto.getAccessKeyId());
        return connectionController.create(dto).getBody().getId();
    }

    private Long createConnectionToHackerRank() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final CreateConnectionDto dto = new CreateConnectionDto();
        dto.setName("Default");
        dto.setConnectorId(connectorId);
        dto.setAccessKeyId(-1L);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(dto.getAccessKeyId());
        return connectionController.create(dto).getBody().getId();
    }

    private CreateBasicAuthorizationDto createCreateBasicAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto();
        final String user = "user_" + randomUUID;
        final String password = "pwd_" + randomUUID;
        dto.setUsername(user);
        dto.setPassword(password);
        return dto;
    }

    private CreateTokenAuthorizationDto createCreateTokenAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateTokenAuthorizationDto dto = new CreateTokenAuthorizationDto();
        final String token = "token_" + randomUUID;
        dto.setToken(token);
        return dto;
    }
}
