package io.electrica.connector.hub.rest;

import com.google.common.collect.Sets;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

public class ConnectionControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorControllerImpl connectorController;

    @Inject
    private ConnectionControllerImpl connectionController;

    @MockBean
    @Inject
    private AccessKeyClient accessKeyClient;


    @Before
    public void setup() {
        super.setup();
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

        final CreateConnectionDto dto = new CreateConnectionDto("Default", null, connectorId, accessKeyId);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        final ConnectionDto actual = connectionController.create(dto).getBody();

        final Connection connection = connectionRepository.findById(actual.getId()).orElse(null);
        assertNotNull(actual.getId());

        assertEquals(connectorId, connection.getConnector().getId());
        assertEquals(Long.valueOf(1), connection.getUserId());
        assertEquals(Long.valueOf(1), connection.getOrganizationId());
        assertEquals(accessKeyId, connection.getAccessKeyId());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {
            PermissionType.CreateConnector,
            PermissionType.AssociateAccessKeyToConnector
    })
    public void testConnectWithUserClientReturnFalse() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;

        final CreateConnectionDto dto = new CreateConnectionDto("Default", null, connectorId, accessKeyId);
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
    @ForUser(userId = 1, organizationId = 1, roles = RoleType.SuperAdmin, permissions = {PermissionType.CreateConnector,
            PermissionType.AssociateAccessKeyToConnector})
    public void testConnectWithSameConnectorAndAccessKey() {
        final Long connectorId = connectorController
                .create(createHackerRankConnectorDto())
                .getBody()
                .getId();

        final Long accessKeyId = 12L;
        final CreateConnectionDto dto = new CreateConnectionDto("Default", null, connectorId, accessKeyId);
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(accessKeyId);
        connectionController.create(dto);

        // assert
        connectionController.create(dto);

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
        final CreateConnectionDto dto = new CreateConnectionDto("Default", null, connectorId, accessKeyId);
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
        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKeyById(Mockito.anyLong());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        PermissionType.AssociateAccessKeyToConnector,
                        PermissionType.ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", null, cnGreenhouse.get(), 1L));
                    connectionController.create(new CreateConnectionDto("Test1", null, cnHackerRank.get(), 1L));
                    connectionController.create(new CreateConnectionDto("Test2", null, cnHackerRank.get(), 2L));
                });


        executeForUser(2, 2,
                Sets.newHashSet(RoleType.OrgAdmin),
                Sets.newHashSet(
                        PermissionType.AssociateAccessKeyToConnector,
                        PermissionType.ReadActiveConnection), () -> {
                    connectionController.create(new CreateConnectionDto("Default", null, cnGreenhouse.get(), 3L));
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
