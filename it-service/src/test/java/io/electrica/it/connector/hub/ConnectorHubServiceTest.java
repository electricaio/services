package io.electrica.it.connector.hub;

import io.electrica.connector.hub.dto.*;
import io.electrica.it.BaseIT;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.electrica.it.util.ItServiceConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ConnectorHubServiceTest extends BaseIT {

    private static final String CONNECTOR_URL = "http://connector.dev.electrica.io";
    private static final String SDK_URL = "http://sdk.dev.electrica.io";
    private static final String CONNECTION_NAME_PREFIX = "connection-";
    private static final Long DEFAULT_CONNECTOR_TYPE = 1L;
    private final Logger logger = LoggerFactory.getLogger(ConnectorHubServiceTest.class);

    private static final Map<String, String> TEST_CONNECTOR_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

    @BeforeClass
    public void setup() {
        contextHolder.clear();
    }

    @Test(groups = {FILL_DATA_GROUP}, dependsOnGroups = {USER_SERVICE_GROUP})
    public void testAddConnectors() {
        contextHolder.setContextForAdmin();
        List<ConnectorDto> connectors = connectorClient.findAll().getBody();
        createLeverConnector(connectors, "V1");

    }

    private void createLeverConnector(List<ConnectorDto> connectors, String version) {
        String connectorName = createConnectorName("Lever", version);
        Optional<ConnectorDto> optionalConnector = connectors.stream()
                .filter(c -> Objects.equals(c.getName(), connectorName)).findAny();
        if (!(optionalConnector.isPresent())) {
            CreateConnectorDto dto = new CreateConnectorDto(DEFAULT_CONNECTOR_TYPE,
                    AuthorizationType.Token, connectorName, "channel", version.toLowerCase(), "lever",
                    "https://github.com/lever/postings-api/", CONNECTOR_URL, SDK_URL,
                    "https://assets.themuse.com/uploaded/companies/773/small_logo.png", "test desciption",
                    TEST_CONNECTOR_PROPERTIES);
            connectorClient.create(dto);
        }
    }

    @Test(groups = {FILL_DATA_GROUP}, dependsOnMethods = {"testAddConnectors"})
    public void testAddConnections() {

        contextHolder.getUsers().stream()
                .forEach(u -> {
                    contextHolder.setContextForUser(u.getEmail());
                    List<AccessKeyDto> accessKeyDtoList = accessKeyClient.findAllNonArchivedByUser(u.getId()).getBody();
                    List<ConnectorDto> connectorDtoList = connectorClient.findAll().getBody();
                    connectorDtoList.stream()
                            .forEach(c -> {
                                accessKeyDtoList.stream().forEach(a -> {
                                    ConnectionDto connection = createConnection(createConnectionName(),
                                            c.getId(), a.getId());
                                    setAuthorization(connection, c.getAuthorizationType());
                                });
                            });
                });
    }

    @Test(groups = {TEST_GROUP}, dependsOnGroups = {INIT_GROUP, FILL_DATA_GROUP})
    public void testFindAllConnectors() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        List<ConnectorDto> connectorDtoList = connectorClient.findAll().getBody();
        assertTrue(connectorDtoList.size() > 0);
    }

    @Test(groups = {TEST_GROUP}, dependsOnGroups = {INIT_GROUP, FILL_DATA_GROUP})
    public void testFindAllConnections() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        ConnectorDto connector = connectorClient.findAll().getBody().get(0);
        List<ConnectionDto> connectionDtos = connectionClient.findAllByUser(user.getId(), connector.getId()).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test(groups = {TEST_GROUP}, dependsOnGroups = {INIT_GROUP, FILL_DATA_GROUP})
    public void testFindAllConnectionsWithConnectorId() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        List<ConnectionDto> connectionDtos = connectionClient.findAllByUser(user.getId(), null).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test(groups = {TEST_GROUP}, dependsOnGroups = {INIT_GROUP, FILL_DATA_GROUP})
    public void testGetConnectionById() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        ConnectorDto connector = connectorClient.findAll().getBody().get(0);
        ConnectionDto connection = connectionClient.findAllByUser(user.getId(), connector.getId()).getBody().get(0);
        ConnectionDto actual = connectionClient.get(connection.getId()).getBody();
        assertEquals(connection.getName(), actual.getName());
        assertEquals(connection.getRevisionVersion(), actual.getRevisionVersion());
        assertEquals(connection.getAccessKeyId(), actual.getAccessKeyId());
        assertEquals(connection.getAuthorizationId(), actual.getAuthorizationId());
    }

    @Test(groups = {TEST_GROUP}, dependsOnGroups = {INIT_GROUP, FILL_DATA_GROUP})
    public void testFindAllByAccessKeyById() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        List<ConnectionDto> connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody();
        assertTrue(connectionDtos.size() > 0);
    }

    @Test(groups = {TEST_GROUP}, dependsOnGroups = {INIT_GROUP, FILL_DATA_GROUP})
    public void testConnectionBelongToCurrentUser() {
        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        Boolean result = connectionClient.connectionBelongsCurrentUser(connectionDtos.getId()).getBody();
        assertTrue(result);
    }

    private void setAuthorization(ConnectionDto connection, AuthorizationType authorizationType) {
        switch (authorizationType) {
            case Basic:
                authorizationClient.authorizeWithBasic(connection.getId(), createBasicAuthorizationDto());
                break;
            case Token:
                authorizationClient.authorizeWithToken(connection.getId(), createTokenAuthorizationDto());
                break;
        }
    }

    private String createConnectionName() {
        return CONNECTION_NAME_PREFIX + getCurrTimeInMillSeconds();
    }

    private String createConnectorName(String name, String version) {
        return name + " " + version;
    }

    private ConnectionDto createConnection(String name, Long connectorId, Long accessKeyId) {
        CreateConnectionDto connectionDto = new CreateConnectionDto();
        connectionDto.setName(name);
        connectionDto.setAccessKeyId(accessKeyId);
        connectionDto.setConnectorId(connectorId);
        return connectionClient.create(connectionDto).getBody();
    }

    private CreateBasicAuthorizationDto createBasicAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto();
        final String user = "user_" + randomUUID;
        final String password = "pwd_" + randomUUID;
        dto.setUsername(user);
        dto.setPassword(password);
        return dto;
    }

    private CreateTokenAuthorizationDto createTokenAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateTokenAuthorizationDto dto = new CreateTokenAuthorizationDto();
        final String token = "token_" + randomUUID;
        dto.setToken(token);
        return dto;
    }

}
