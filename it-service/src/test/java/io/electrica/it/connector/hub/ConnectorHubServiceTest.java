package io.electrica.it.connector.hub;

import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import io.electrica.it.BaseIT;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectorHubServiceTest extends BaseIT {

    @BeforeEach
    void setup() {
        super.init();
    }

    @Test
    public void testAddConnectors() {
        contextHolder.setContextForAdmin();
        createTestConnector("Test-" + getCurrTimeInMillSeconds(), "V1");

    }

    @Test
    public void testFindAllConnectors() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        List<ConnectorDto> connectorDtoList = connectorClient.findAll().getBody();
        assertTrue(connectorDtoList.size() > 0);
    }

    @Test
    public void testFindAllConnectionsWithConnectorId() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        ConnectorDto connector = connectorClient.findAll().getBody().get(0);
        List<ConnectionDto> connectionDtos = connectionClient.findAllByUser(user.getId(), connector.getId()).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test
    public void testFindAllConnections() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        List<ConnectionDto> connectionDtos = connectionClient.findAllByUser(user.getId(), null).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test
    public void testGetConnectionById() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        ConnectionDto connection = getNewConnectionForUser(user);
        ConnectionDto actual = connectionClient.get(connection.getId()).getBody();
        assertEquals(connection.getName(), actual.getName());
        assertEquals(connection.getAccessKeyId(), actual.getAccessKeyId());
        assertNotNull(actual.getAuthorizationId());
    }

    @Test
    public void testFindAllByAccessKeyById() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        List<ConnectionDto> connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test
    public void testAuthorizationTokenUpdate() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
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
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        ConnectionDto con = getNewConnectionForUser(user);
        int connectionCount = connectionClient.findAllByUser(user.getId(), null).getBody().size();
        connectionClient.delete(con.getId());
        int updatedCount = connectionClient.findAllByUser(user.getId(), null).getBody().size();
        assertEquals(connectionCount - 1, updatedCount);
    }

    private ConnectionDto getNewConnectionForUser(UserDto user) {
        AccessKeyDto accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        ConnectorDto connector = connectorClient.findAll().getBody().get(0);
        return createConnection(getCurrTimeAsString(), connector, accessKey.getId());
    }

}
