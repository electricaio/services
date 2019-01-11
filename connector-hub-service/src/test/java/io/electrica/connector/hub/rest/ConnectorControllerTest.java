package io.electrica.connector.hub.rest;

import io.electrica.ConnectorHubServiceApplicationTest;
import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.service.ConnectorService;
import io.electrica.test.context.ForUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ConnectorControllerTest extends ConnectorHubServiceApplicationTest {

    @Inject
    private ConnectorControllerImpl connectorController;

    @Inject
    private ConnectorService connectorService;

    private ConnectorType connectorType;

    @Before
    public void setup() {
        super.setup();
        connectorType = findConnectorType("Talent");
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithSuccess() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        final ConnectorDto actual = connectorController.create(dto).getBody();
        compare(dto, actual);
    }

    private void compare(CreateConnectorDto dto, ConnectorDto actual) {
        assertNotNull(actual.getId());
        assertNotNull(actual.getRevisionVersion());

        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getNamespace(), actual.getNamespace());
        assertEquals(dto.getVersion(), actual.getVersion());
        assertEquals(dto.getResource(), actual.getResource());
        assertEquals(connectorType.getId(), actual.getTypeId());
        assertNotNull(actual.getErn());
        assertTrue(dto.getProperties().equals(actual.getProperties()));
        assertEquals(dto.getSourceUrl(), actual.getSourceUrl());
        assertEquals(dto.getSdkUrl(), actual.getSdkUrl());
        assertEquals(dto.getConnectorUrl(), actual.getConnectorUrl());
        assertEquals(dto.getImageUrl(), actual.getImageUrl());
        assertEquals(dto.getDescription(), actual.getDescription());
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithSuccessWithNullProperties() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        dto.setProperties(null);
        final ConnectorDto actual = connectorController.create(dto).getBody();

        assertNotNull(actual.getId());
        assertNotNull(actual.getRevisionVersion());

        assertEquals(dto.getName(), actual.getName());
        assertEquals(dto.getNamespace(), actual.getNamespace());
        assertEquals(dto.getVersion(), actual.getVersion());
        assertEquals(dto.getResource(), actual.getResource());

        assertEquals(connectorType.getId(), actual.getTypeId());

        final String expectedErn = "ern://com_hackerrank:applications:1_0";
        assertEquals(expectedErn, actual.getErn());
        assertNull(actual.getProperties());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithNonExistingSTLType() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        dto.setTypeId(-1L);
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
        connectorController.create(createHackerRankConnectorDto());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.OrgAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testCreateConnectorWithWrongRole() {
        connectorController.create(createHackerRankConnectorDto());
    }

    @Test(expected = ConstraintViolationException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testToVerifyStringLength() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        String inputString = "aDtQXnQGapVspYgRIyFgonXYClSxEhmBNMCWeAa07XKQWSJZEkMegGyiceIzmwbH2DbQ6eXraSeRCIYSn5XA" +
                "Z2G1V3ExOmnI8hAKrgD8eqM58ls07uD1i7SBqsKMpP4oDw3g6ZLhbSO15CPKGMs0HuHt8L9Mo1qg57UumypZYybuKkfZVOj89" +
                "SxsuElxycWvqA3gikDQh7sugDst4j0Rc0uf9HSidLjcPeR9BMDsiqaR5VhIx7KlLg21bV2XUUEPEuOb";
        dto.setConnectorUrl(inputString);
        dto.setSourceUrl(inputString);
        dto.setImageUrl(inputString);
        dto.setSdkUrl(inputString);
        dto.setDescription(inputString);
        final ConnectorDto actual = connectorController.create(dto).getBody();
        flushAndClear();
    }

    @Test
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testFindAllNonArchived() {
        ConnectorDto hackerRankDto = connectorController.create(createHackerRankConnectorDto()).getBody();

        ConnectorDto greenHouseDto = connectorController.create(createGreenhouseConnectorDto()).getBody();
        connectorService.archive(greenHouseDto.getId());

        executeForUser(1L, 1L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadConnector), () -> {
                    List<ConnectorDto> allConnectors = connectorController.findAll().getBody();
                    ConnectorDto actualHackerRankDto = allConnectors.stream()
                            .filter(c -> Objects.equals(c.getId(), hackerRankDto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("Connector not found: " + hackerRankDto.getId()));
                    assertEquals(hackerRankDto.getName(), actualHackerRankDto.getName());

                    ConnectorDto actualGreenHouseDto = allConnectors.stream()
                            .filter(c -> Objects.equals(c.getId(), greenHouseDto.getId()))
                            .findFirst()
                            .orElse(null);
                    assertNull("Must be archived", actualGreenHouseDto);
                });
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(
            userId = 1,
            organizationId = 1,
            roles = RoleType.SuperAdmin,
            permissions = PermissionType.CreateConnector
    )
    public void testFindAllNonArchivedWithNoreadPermission() {
        final CreateConnectorDto createHackerRankSTLDto = createHackerRankConnectorDto();
        connectorController.create(createHackerRankSTLDto);

        final ConnectorDto greenHouseDto = connectorController.create(createGreenhouseConnectorDto()).getBody();
        connectorService.archive(greenHouseDto.getId());

        executeForUser(1L, 1L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.CreateConnector), () -> {
                    connectorController.findAll().getBody();
                });
    }

    @Test
    public void testGetConnectorById() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        final AtomicReference<ConnectorDto> connector = new AtomicReference<>();

        executeForUser(1L, 1L, EnumSet.of(RoleType.SuperAdmin),
                EnumSet.of(PermissionType.CreateConnector), () -> {
                    final ConnectorDto connectorDto = connectorController.create(dto).getBody();
                    connector.set(connectorDto);
                });

        flushAndClear();

        executeForUser(1L, 1L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadConnector), () -> {
                    ConnectorDto actual = connectorController.getConnector(connector.get().getId()).getBody();
                    compare(dto, actual);
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetConnectorByIdWithoutReadPermission() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        final AtomicReference<ConnectorDto> connector = new AtomicReference<>();

        executeForUser(1L, 1L, EnumSet.of(RoleType.SuperAdmin),
                EnumSet.of(PermissionType.CreateConnector), () -> {
                    final ConnectorDto connectorDto = connectorController.create(dto).getBody();
                    connector.set(connectorDto);
                });

        flushAndClear();

        executeForUser(1L, 1L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadActiveConnection), () -> {
                    connectorController.getConnector(connector.get().getId()).getBody();
                });
    }

    @Test(expected = EntityNotFoundServiceException.class)
    public void testGetConnectorByIdWithIDNotExist() {
        final CreateConnectorDto dto = createHackerRankConnectorDto();
        final AtomicReference<ConnectorDto> connector = new AtomicReference<>();

        executeForUser(1L, 1L, EnumSet.of(RoleType.SuperAdmin),
                EnumSet.of(PermissionType.CreateConnector), () -> {
                    final ConnectorDto connectorDto = connectorController.create(dto).getBody();
                    connector.set(connectorDto);
                });

        flushAndClear();

        executeForUser(1L, 1L, EnumSet.of(RoleType.OrgUser),
                EnumSet.of(PermissionType.ReadConnector), () -> {
                    connectorController.getConnector(-22L).getBody();
                });
    }
}
