package io.electrica.connector.hub.rest;

import com.google.common.collect.Sets;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionControllerTest extends AbstractDatabaseTest {

    @Inject
    private ConnectorControllerImpl connectorController;

    @Inject
    private ConnectionControllerImpl connectionController;

    private AuthorizationType tokenAuthType;

    private AuthorizationType basicAuthType;

    @Before
    public void setup() {
        super.setup();
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
