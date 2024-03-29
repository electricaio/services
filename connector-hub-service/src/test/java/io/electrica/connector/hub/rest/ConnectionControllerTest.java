package io.electrica.connector.hub.rest;

import com.google.common.collect.Sets;
import io.electrica.ConnectorHubServiceApplicationTest;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.*;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.dto.sdk.TokenTypedAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.TypedAuthorizationDto;
import io.electrica.connector.hub.model.Connection;
import io.electrica.test.context.ForUser;
import io.electrica.user.feign.AccessKeyClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static io.electrica.common.security.PermissionType.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class ConnectionControllerTest extends ConnectorHubServiceApplicationTest {

    @Inject
    private ConnectorControllerImpl connectorController;

    @Inject
    private ConnectionControllerImpl connectionController;

    @Inject
    private AuthorizationControllerImpl authorizationController;

    @MockBean
    @Inject
    private AccessKeyClient accessKeyClient;


    @Before
    public void setup() {
        super.setup();
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector,
            AssociateAccessKeyToConnector
    })
    public void testConnectWithSuccess() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        final Connection connection = connectionRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());

        assertEquals(connectorId, connection.getConnector().getId());
        assertEquals(Long.valueOf(1), connection.getUserId());
        assertEquals(Long.valueOf(1), connection.getOrganizationId());
        assertEquals(accessKeyId, connection.getAccessKeyId());
        assertTrue(dto.getProperties().equals(connection.getProperties()));
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector
    })
    public void testConnectWithSuccessWithPropertiesNull() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        dto.setProperties(null);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        final Connection connection = connectionRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());

        assertEquals(connectorId, connection.getConnector().getId());
        assertEquals(Long.valueOf(1), connection.getUserId());
        assertEquals(Long.valueOf(1), connection.getOrganizationId());
        assertEquals(accessKeyId, connection.getAccessKeyId());
        assertNull(connection.getProperties());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector
    })
    public void testConnectWithUserClientReturnFalse() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(false)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        final Connection connection = connectionRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());

        assertEquals(connectorId, connection.getConnector().getId());
        assertEquals(Long.valueOf(1), connection.getUserId());
        assertEquals(Long.valueOf(1), connection.getOrganizationId());
        assertEquals(accessKeyId, connection.getAccessKeyId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {CreateConnector,
            AssociateAccessKeyToConnector})
    public void testConnectWithSameConnectorAndAccessKey() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;
        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        connectionController.create(dto);

        // assert
        connectionController.create(dto);

        connectionRepository.flush();
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.OrgUser, permissions = CreateConnector)
    public void testConnectWithWrongPermissions() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;
        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        connectionController.create(dto);
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

        final AtomicReference<Long> cnHackerRank = createHackerRankConnector();
        final AtomicReference<Long> cnGreenhouse = createGreenHouseConnector();
        final AtomicReference<Long> cnMySQL = createMySQLConnector();

        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(Mockito.anyLong());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        AssociateAccessKeyToConnector, ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", cnGreenhouse.get(), 1L,
                            TEST_PROPERTIES));
                    connectionController.create(new CreateConnectionDto("Test1", cnHackerRank.get(), 1L,
                            TEST_PROPERTIES));
                    connectionController.create(new CreateConnectionDto("Test2", cnHackerRank.get(), 2L,
                            TEST_PROPERTIES));
                });


        executeForUser(2, 2,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        AssociateAccessKeyToConnector, ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", cnGreenhouse.get(), 3L,
                            TEST_PROPERTIES));
                });

        flushAndClear();

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(ReadActiveConnection), () -> {

                    final List<ConnectionDto> result = connectionController.findAllByUser(1L, null).getBody();
                    assertEquals(3, result.size());
                    assertEquals(cnGreenhouse.get(), result.get(0).getConnectorId());
                    assertEquals(Long.valueOf(1), result.get(0).getAccessKeyId());
                    assertNotNull(result.get(0).getCreatedAt());
                    assertNotNull(result.get(0).getUpdatedAt());
                    assertEquals(cnHackerRank.get(), result.get(1).getConnectorId());
                    assertEquals(Long.valueOf(1), result.get(1).getAccessKeyId());
                    assertNotNull(result.get(1).getCreatedAt());
                    assertNotNull(result.get(1).getUpdatedAt());
                    assertEquals(cnHackerRank.get(), result.get(2).getConnectorId());
                    assertEquals(Long.valueOf(2), result.get(2).getAccessKeyId());
                    assertNotNull(result.get(2).getCreatedAt());
                    assertNotNull(result.get(2).getUpdatedAt());
                });

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(ReadActiveConnection), () -> {

                    final List<ConnectionDto> result = connectionController.findAllByUser(
                            1L, cnGreenhouse.get()).getBody();
                    assertEquals(1, result.size());
                    assertEquals(cnGreenhouse.get(), result.get(0).getConnectorId());
                    assertEquals(Long.valueOf(1), result.get(0).getAccessKeyId());
                    assertNotNull(result.get(0).getCreatedAt());
                    assertNotNull(result.get(0).getUpdatedAt());
                });

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(ReadActiveConnection), () -> {

                    final List<ConnectionDto> result = connectionController.findAllByUser(
                            1L, cnHackerRank.get()).getBody();
                    assertEquals(2, result.size());
                    assertEquals(cnHackerRank.get(), result.get(0).getConnectorId());
                    assertEquals(Long.valueOf(1), result.get(0).getAccessKeyId());
                    assertNotNull(result.get(0).getCreatedAt());
                    assertNotNull(result.get(0).getUpdatedAt());

                    assertEquals(cnHackerRank.get(), result.get(1).getConnectorId());
                    assertEquals(Long.valueOf(2), result.get(1).getAccessKeyId());
                    assertNotNull(result.get(1).getCreatedAt());
                    assertNotNull(result.get(1).getUpdatedAt());
                });

        executeForUser(2, 2,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(ReadActiveConnection), () -> {
                    final List<ConnectionDto> result = connectionController.findAllByUser(2L, null).getBody();
                    assertEquals(1, result.size());
                    assertEquals(cnGreenhouse.get(), result.get(0).getConnectorId());
                    assertEquals(Long.valueOf(3), result.get(0).getAccessKeyId());
                });


        executeForUser(3, 3,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(ReadActiveConnection), () -> {
                    final List<ConnectionDto> result = connectionController.findAllByUser(3L, null).getBody();
                    assertEquals(0, result.size());
                });
    }

    @Test
    public void getFullConnectionTest() {

        long accessKeyId = 12L;
        AtomicLong connectionId = new AtomicLong();

        createFullConnection(accessKeyId, connectionId);

        executeForAccessKey(1, accessKeyId, () -> {
            FullConnectionDto connectionDto = connectionController.getFull(connectionId.get()).getBody();

            ConnectionDto connection = connectionDto.getConnection();
            assertNotNull(connection);
            assertEquals(connectionId.get(), connection.getId().longValue());
            assertEquals(accessKeyId, connection.getAccessKeyId().longValue());

            ConnectorDto connector = connectionDto.getConnector();
            assertNotNull(connector);
            assertEquals("HackerRank", connector.getName());

            TypedAuthorizationDto authorization = connectionDto.getAuthorization();
            assertNotNull(authorization);
            assertTrue(authorization instanceof TokenTypedAuthorizationDto);
            assertEquals("test", ((TokenTypedAuthorizationDto) authorization).getData().getToken());
        });
    }

    @Test(expected = AccessDeniedException.class)
    public void getFullConnectionForAnotherAccessKeyDeniedTest() {

        long accessKeyId = 12L;
        AtomicLong connectionId = new AtomicLong();

        createFullConnection(accessKeyId, connectionId);

        executeForAccessKey(1, accessKeyId + 1, () -> {
            connectionController.getFull(connectionId.get()).getBody();
        });
    }

    private void createFullConnection(long accessKeyId, AtomicLong connectionId) {
        executeForUser(
                1,
                1,
                Sets.newHashSet(RoleType.SuperAdmin),
                Sets.newHashSet(CreateConnector, AssociateAccessKeyToConnector),
                () -> {
                    Long connectorId = connectorController
                            .create(createHackerRankConnectorDto())
                            .getBody()
                            .getId();

                    CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                            TEST_PROPERTIES);
                    doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
                    connectionId.set(connectionController.create(dto).getBody().getId());

                    CreateTokenAuthorizationDto token = new CreateTokenAuthorizationDto();
                    token.setToken("test");
                    authorizationController.authorizeWithToken(connectionId.get(), token);
                }
        );
    }

    @Test
    public void testForGetConnectionsForMeSuccess() {
        final AtomicReference<Long> cnHackerRank = createHackerRankConnector();
        final AtomicReference<Long> cnGreenhouse = createGreenHouseConnector();
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(Mockito.anyLong());
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKey();

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        AssociateAccessKeyToConnector, ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", cnGreenhouse.get(), 1L,
                            TEST_PROPERTIES));
                    connectionController.create(new CreateConnectionDto("Test1", cnHackerRank.get(), 1L,
                            TEST_PROPERTIES));
                    connectionController.create(new CreateConnectionDto("Test2", cnHackerRank.get(), 2L,
                            TEST_PROPERTIES));
                });

        executeForUser(2, 2, Sets.newHashSet(RoleType.OrgAdmin), Sets.newHashSet(
                AssociateAccessKeyToConnector, ReadActiveConnection), () -> {
            connectionController.create(new CreateConnectionDto("Default",
                    cnGreenhouse.get(), 3L, TEST_PROPERTIES));
        });

        AtomicReference<List<ConnectorDto>> connectorDtoList = new AtomicReference<>();
        executeForUser(2, 2, Sets.newHashSet(RoleType.OrgAdmin), Sets.newHashSet(
                ReadConnector), () -> {
            connectorDtoList.set(connectorController.findAll().getBody());
        });

        String greenhouseErn = connectorDtoList.get().stream()
                .filter(c -> Objects.equals(c.getId(), cnGreenhouse.get()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("connector not found: " + cnGreenhouse.get()))
                .getErn();

        executeForAccessKey(1, 1, () -> {
            List<ConnectionDto> result = connectionController.findAllByAccessKey(null,
                    greenhouseErn).getBody();
            assertEquals(1, result.size());
            result = connectionController.findAllByAccessKey("Default",
                    greenhouseErn).getBody();
            assertEquals(1, result.size());
            assertEquals("Default", result.get(0).name);
        });
    }

    @Test
    public void testFindAllByAccessKeySuccess() {
        final AtomicReference<Long> cnHackerRank = createHackerRankConnector();
        final AtomicReference<Long> cnGreenhouse = createGreenHouseConnector();
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(Mockito.anyLong());

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        AssociateAccessKeyToConnector,
                        ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", cnGreenhouse.get(), 1L,
                            TEST_PROPERTIES));
                    connectionController.create(new CreateConnectionDto("Test1", cnHackerRank.get(), 1L,
                            TEST_PROPERTIES));
                    connectionController.create(new CreateConnectionDto("Test2", cnHackerRank.get(), 2L,
                            TEST_PROPERTIES));
                });
        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(DeleteAccessKey, ReadActiveConnection),
                () -> {
                    List<ConnectionDto> result = connectionController.findAllByAccessKeyId(1L).getBody();
                    assertEquals(2, result.size());
                    assertEquals("Default", result.get(0).name);
                    assertEquals("Test1", result.get(1).name);

                    result = connectionController.findAllByAccessKeyId(2L).getBody();
                    assertEquals(1, result.size());
                    assertEquals("Test2", result.get(0).name);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testFindAllByAccessKeyWithoutReadActiveConnectionPermission() {
        final AtomicReference<Long> cnGreenhouse = createGreenHouseConnector();
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(Mockito.anyLong());

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        AssociateAccessKeyToConnector, ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", cnGreenhouse.get(), 1L,
                            TEST_PROPERTIES));
                });
        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(DeleteAccessKey,
                ReadAccessKey),
                () -> {
                    List<ConnectionDto> result = connectionController.findAllByAccessKeyId(1L).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testFindAllByAccessKeyBelongToDiffUser() {
        final AtomicReference<Long> cnGreenhouse = createGreenHouseConnector();
        doReturn(ResponseEntity.ok(false)).when(accessKeyClient).validateMyAccessKeyById(Mockito.anyLong());

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        AssociateAccessKeyToConnector, ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", cnGreenhouse.get(),
                            1L, TEST_PROPERTIES));
                });
        executeForUser(-1L, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(DeleteAccessKey,
                ReadActiveConnection),
                () -> {
                    List<ConnectionDto> result = connectionController.findAllByAccessKeyId(1L).getBody();
                });
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector
    })
    public void testValidateConnection() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto connection = connectionController.create(dto).getBody();

        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(
                ReadActiveConnection), () -> {
            assertTrue(connectionController.connectionBelongsCurrentUser(connection.getId()).getBody());
        });

    }


    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector
    })
    public void testValidateConnectionWithConnectionBelongToDiffUser() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto connection = connectionController.create(dto).getBody();

        executeForUser(2, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(
                ReadActiveConnection), () -> {
            assertTrue(connectionController.connectionBelongsCurrentUser(connection.getId()).getBody());
        });

    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector
    })
    public void testValidateConnectionWithNoReadConnectionPermission() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto connection = connectionController.create(dto).getBody();

        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(
                AddPermission), () -> {
            assertTrue(connectionController.connectionBelongsCurrentUser(connection.getId()).getBody());
        });

    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector, ReadActiveConnection})
    public void testDeleteConnection() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;
        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(DeActivateConnection), () -> {
            connectionController.delete(actual.getId());
        });

        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(ReadActiveConnection), () -> {
            List<ConnectionDto> result = connectionController.findAllByUser(1L, null).getBody();
            assertTrue(result.size() == 0);
        });
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector, ReadActiveConnection})
    public void testDeleteConnectionWithoutPermission() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;
        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        executeForUser(1, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(ReadActiveConnection), () -> {
            connectionController.delete(actual.getId());
        });
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector, AssociateAccessKeyToConnector, ReadActiveConnection})
    public void testDeleteConnectionBelongToDiffUser() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;
        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        executeForUser(2, 1L, EnumSet.of(RoleType.OrgUser), EnumSet.of(DeActivateConnection), () -> {
            connectionController.delete(actual.getId());
        });
    }


    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector,
            AssociateAccessKeyToConnector
    })
    public void testUpdateWithoutPermission() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto connection = connectionController.create(dto).getBody();

        connectionController.update(connection.getId(), new UpdateConnectionDto());
    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector,
            UpdateConnection,
            ReadActiveConnection,
            AssociateAccessKeyToConnector
    })
    public void testUpdateProperties() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto connection = connectionController.create(dto).getBody();

        UpdateConnectionDto updateConnectionDto = new UpdateConnectionDto();
        updateConnectionDto.setId(connection.getId());
        updateConnectionDto.setRevisionVersion(connection.getRevisionVersion());

        String updateKey = "new key";
        String updateValue = "new key";

        Map<String, String> properties = new HashMap<>();
        properties.put(updateKey, updateValue);
        updateConnectionDto.setProperties(properties);

        ResponseEntity<ConnectionDto> update = connectionController.update(connection.getId(), updateConnectionDto);
        assertEquals(update.getBody().getProperties().get(updateKey), updateValue);

    }

    @Test
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            CreateConnector,
            UpdateConnection,
            ReadActiveConnection,
            AssociateAccessKeyToConnector
    })
    public void testUpdateFromPermittedUser() {

        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", connectorId, accessKeyId,
                TEST_PROPERTIES);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto connection = connectionController.create(dto).getBody();

        String updatedName = "NEW NAME";

        UpdateConnectionDto updateConnectionDto = new UpdateConnectionDto();
        updateConnectionDto.setName(updatedName);
        updateConnectionDto.setId(connection.getId());
        updateConnectionDto.setRevisionVersion(connection.getRevisionVersion());

        ResponseEntity<ConnectionDto> update = connectionController.update(connection.getId(), updateConnectionDto);

        assertEquals(update.getBody().getName(), updatedName);
    }


    private AtomicReference<Long> createHackerRankConnector() {
        return createConnector(createHackerRankConnectorDto());
    }

    private AtomicReference<Long> createGreenHouseConnector() {
        return createConnector(createGreenhouseConnectorDto());
    }

    private AtomicReference<Long> createMySQLConnector() {
        return createConnector(createMySQLConnectorDto());
    }

    private AtomicReference<Long> createConnector(CreateConnectorDto dto) {
        final AtomicReference<Long> connector = new AtomicReference<>();
        executeForUser(-1, -1,
                Sets.newHashSet(RoleType.SuperAdmin),
                Sets.newHashSet(CreateConnector),
                () -> {
                    connector.set(connectorController
                            .create(dto)
                            .getBody().getId()
                    );
                }
        );
        return connector;
    }
}
