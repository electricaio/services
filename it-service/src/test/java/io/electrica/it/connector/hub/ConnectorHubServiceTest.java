package io.electrica.it.connector.hub;

import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.*;
import io.electrica.it.BaseIT;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class ConnectorHubServiceTest extends BaseIT {

    private UserDto user;

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setContextForUser(user.getEmail());
        getNewConnectionForUser(user);
    }

    @BeforeEach
    void setContext() {
        contextHolder.setContextForUser(user.getEmail());
    }

    @Test
    public void testAddConnectors() {
        contextHolder.setContextForAdmin();
        createConnector("Test-" + getCurrTimeInMillSeconds(), "V1");
    }

    @Test
    public void testFindAllConnectors() {
        List<ConnectorDto> connectorDtoList = connectorClient.findAll().getBody();
        assertTrue(connectorDtoList.size() > 0);
    }

    @Test
    public void testFindAllConnectionsWithConnectorId() {
        ConnectorDto connector = connectorClient.findAll().getBody().get(0);
        List<ConnectionDto> connectionDtos = connectionClient.findAllByUser(user.getId(), connector.getId()).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test
    public void testFindAllConnections() {
        List<ConnectionDto> connectionDtos = connectionClient.findAllByUser(user.getId(), null).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test
    public void testGetConnectionById() {
        ConnectionDto connection = getNewConnectionForUser(user);
        ConnectionDto actual = connectionClient.get(connection.getId()).getBody();
        assertEquals(connection.getName(), actual.getName());
        assertEquals(connection.getAccessKeyId(), actual.getAccessKeyId());
        assertNotNull(actual.getAuthorizationId());
    }

    @Test
    public void testFindAllByAccessKeyById() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        List<ConnectionDto> connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test
    public void testAuthorizationTokenUpdate() {
        AccessKeyDto accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        ConnectorDto connector = connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getAuthorizationType(), AuthorizationType.Token))
                .findFirst().get();
        ConnectionDto connection = createConnection(getCurrTimeAsString(), connector, accessKey.getId());

        TokenAuthorizationDto token = authorizationClient.getToken(connection.getAuthorizationId()).getBody();
        String tokenStr = getCurrTimeInMillSeconds().toString();
        token.setToken(tokenStr);
        authorizationClient.updateToken(token.getId(), token).getBody();

        TokenAuthorizationDto dto = authorizationClient.getToken(connection.getAuthorizationId()).getBody();
        assertEquals(tokenStr, dto.getToken());
    }

    @Test
    public void testDeleteConnection() {
        ConnectionDto con = getNewConnectionForUser(user);
        int connectionCount = connectionClient.findAllByUser(user.getId(), null).getBody().size();
        connectionClient.delete(con.getId());
        int updatedCount = connectionClient.findAllByUser(user.getId(), null).getBody().size();
        assertEquals(connectionCount - 1, updatedCount);
    }

    @Test
    public void testUpdateConnection() {
        ConnectionDto connection = getNewConnectionForUser(user);

        UpdateConnectionDto updateConnectionDto = new UpdateConnectionDto();
        updateConnectionDto.setId(connection.getId());
        updateConnectionDto.setRevisionVersion(connection.getRevisionVersion());
        connection.setName("updated name");

        ConnectionDto connectionDto = connectionClient.update(connection.getId(), updateConnectionDto).getBody();

        assertEquals(connectionDto.getName(), updateConnectionDto.getName());

    }
}
