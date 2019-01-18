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
        contextHolder.setTokenForUser(user.getEmail());
        createConnectionForUser(user);
    }

    @BeforeEach
    void setContext() {
        contextHolder.setTokenForUser(user.getEmail());
    }

    @Test
    public void testAddConnectors() {
        contextHolder.setTokenForAdmin();
        createConnector("Test-" + getCurrTimeInMillSeconds(), "V1");
    }

    @Test
    public void testFindAllConnectors() {
        List<ConnectorDto> connectorDtoList = connectorClient.findAll().getBody();
        assertTrue(connectorDtoList.size() > 0);
    }

    @Test
    public void testFindAllConnectionsWithConnectorId() {
        ConnectorDto connector = findConnector(SLACK_CHANNEL_V1_ERN);
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
        ConnectionDto connection = createConnectionForUser(user);
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
        ConnectionDto con = createConnectionForUser(user);
        int connectionCount = connectionClient.findAllByUser(user.getId(), null).getBody().size();
        connectionClient.delete(con.getId());
        int updatedCount = connectionClient.findAllByUser(user.getId(), null).getBody().size();
        assertEquals(connectionCount - 1, updatedCount);
    }

    @Test
    public void testUpdateConnection() {
        ConnectionDto newConnection = createConnectionForUser(user);
        UpdateConnectionDto updateConnectionDto = new UpdateConnectionDto();
        updateConnectionDto.setId(newConnection.getId());
        updateConnectionDto.setRevisionVersion(newConnection.getRevisionVersion());
        updateConnectionDto.setName("updated name");
        ConnectionDto updatedConnection = connectionClient.update(newConnection.getId(), updateConnectionDto).getBody();
        assertEquals(updateConnectionDto.getName(), updatedConnection.getName());
    }
}
