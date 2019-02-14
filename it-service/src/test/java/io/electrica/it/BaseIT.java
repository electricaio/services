package io.electrica.it;

import io.electrica.common.config.EnvironmentTypeConfig;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.*;
import io.electrica.connector.hub.feign.AuthorizationClient;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.connector.hub.feign.ConnectorClient;
import io.electrica.it.auth.TokenManager;
import io.electrica.it.context.ContextHolder;
import io.electrica.it.report.GenerateTestReport;
import io.electrica.it.rule.ServiceAwaitExtension;
import io.electrica.it.util.ReportContext;
import io.electrica.user.dto.*;
import io.electrica.user.feign.AccessKeyClient;
import io.electrica.user.feign.OrganizationClient;
import io.electrica.user.feign.UserClient;
import io.electrica.webhook.feign.WebhookManagementClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.electrica.sdk.java8.slack.channel.v1.SlackChannelV1.CHANNEL_NAME_PROPERTY_KEY;

@SpringBootTest
@ActiveProfiles(EnvironmentTypeConfig.TEST_ENV_PROFILE_NAME)
@ExtendWith({SpringExtension.class, GenerateTestReport.class, ServiceAwaitExtension.class})
public abstract class BaseIT {

    protected static final String ORG_HACKER_RANK = "HackerRank";
    protected static final String ORG_TOP_CODER = "TopCoder";
    protected static final String SLACK_CHANNEL_V1_ERN = "ern://slack:channel:1";
    private static final String USER_NAME_PREFIX = "user-";
    private static final String EMAIL_POSTFIX = "@electrica.io";
    private static final Long DEFAULT_CONNECTOR_TYPE = 1L;
    private static final Map<String, String> TEST_CONNECTOR_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};
    private static final String CONNECTION_NAME_PREFIX = "connection-";
    private static Boolean initialized = false;

    @Inject
    protected UserClient userClient;
    @Inject
    protected OrganizationClient organizationClient;
    @Inject
    protected TokenManager tokenManager;
    @Inject
    protected ContextHolder contextHolder;
    @Inject
    protected AccessKeyClient accessKeyClient;
    @Inject
    protected ConnectorClient connectorClient;
    @Inject
    protected ConnectionClient connectionClient;
    @Inject
    protected AuthorizationClient authorizationClient;
    @Inject
    protected WebhookManagementClient webhookManagementClient;

    @Value("${it-service.slack.v1.webhook-token}")
    protected String slackV1WebhookToken;
    @Value("${it-service.slack.v1.test-result-channel}")
    protected String slackTestResultChannel;
    @Value("${it-service.stand.url}")
    protected String standUrl;
    @Value("${it-service.publish-report}")
    protected Boolean publishReport;

    protected void init() {
        createOrganization(ORG_HACKER_RANK);
        setReportingContext();
    }

    private void setReportingContext() {
        if (!initialized) {
            UserDto user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
            contextHolder.setTokenForUser(user.getEmail());
            AccessKeyDto accessKeyDto = createAccessKey(user.getId(), "Report-" + getCurrTimeAsString());
            FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();

            ReportContext context = ReportContext.getInstance();
            context.setStandUrl(standUrl);
            context.setAccessKey(fullAccessKeyDto.getKey());
            context.setPublishReport(publishReport);
            context.setSlackConnectionName(createSlackConnections(accessKeyDto.getId()).getName());
            context.setChannelName(slackTestResultChannel);
            initialized = true;
        }
    }

    private ConnectionDto createSlackConnections(Long accessKey) {
        ConnectorDto slackV1Connector = connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getErn(), SLACK_CHANNEL_V1_ERN))
                .findFirst().get();
        return createConnection(getConnectionName(), slackV1Connector, accessKey);
    }


    public String getConnectionName() {
        return CONNECTION_NAME_PREFIX + getCurrTimeInMillSeconds();
    }

    public void setAuthorization(ConnectionDto connection, AuthorizationType authorizationType) {
        switch (authorizationType) {
            case Basic:
                authorizationClient.authorizeWithBasic(connection.getId(), createBasicAuthorizationDto());
                break;
            case Token:
                authorizationClient.authorizeWithToken(connection.getId(), createTokenAuthorizationDto(connection));
                break;
        }
    }

    public ConnectionDto createConnection(String name, ConnectorDto connector, Long accessKeyId) {
        CreateConnectionDto connectionDto = new CreateConnectionDto();
        connectionDto.setName(name);
        connectionDto.setAccessKeyId(accessKeyId);
        connectionDto.setConnectorId(connector.getId());
        setPropertiesForConnection(connectionDto, connector.getId());
        ConnectionDto con = connectionClient.create(connectionDto).getBody();
        setAuthorization(con, connector.getAuthorizationType());
        return connectionClient.get(con.getId()).getBody();
    }

    public void setPropertiesForConnection(CreateConnectionDto connectionDto, Long connectorId) {
        ConnectorDto connectorDto = connectorClient.getConnector(connectorId).getBody();
        Map<String, String> properties = new HashMap<>();
        switch (connectorDto.getErn()) {
            case SLACK_CHANNEL_V1_ERN:
                properties.put(CHANNEL_NAME_PROPERTY_KEY, slackTestResultChannel);
                connectionDto.setProperties(properties);
                break;
        }
    }

    public CreateBasicAuthorizationDto createBasicAuthorizationDto() {
        final String randomUUID = UUID.randomUUID().toString().substring(0, 6);
        final CreateBasicAuthorizationDto dto = new CreateBasicAuthorizationDto();
        final String user = "user_" + randomUUID;
        final String password = "pwd_" + randomUUID;
        dto.setUsername(user);
        dto.setPassword(password);
        return dto;
    }

    public CreateTokenAuthorizationDto createTokenAuthorizationDto(ConnectionDto connection) {
        ConnectorDto connectorDto = connectorClient.getConnector(connection.getConnectorId()).getBody();
        final CreateTokenAuthorizationDto dto = new CreateTokenAuthorizationDto();
        switch (connectorDto.getErn()) {
            case SLACK_CHANNEL_V1_ERN:
                dto.setToken(slackV1WebhookToken);
                break;
            default:
                dto.setToken("Test token");
        }
        return dto;
    }

    public UserDto createUser(String org, RoleType roleType) {
        String name = USER_NAME_PREFIX + getCurrTimeInMillSeconds();
        Long orgId = contextHolder.getOrganizationByName(org).getId();
        CreateUserDto user = new CreateUserDto();
        user.setOrganizationId(orgId);
        user.setFirstName(name);
        user.setLastName(name);
        user.setEmail(name + EMAIL_POSTFIX);
        user.setLastName(name);
        user.setPassword(name);
        UserDto userDto = userClient.createUser(user).getBody();
        contextHolder.addUserToContext(userDto);
        return userDto;
    }

    public AccessKeyDto createAccessKey(Long userId, String name) {
        CreateAccessKeyDto accessKeyDto = new CreateAccessKeyDto();
        accessKeyDto.setName(name);
        accessKeyDto.setUserId(userId);
        return accessKeyClient.createAccessKey(accessKeyDto).getBody();
    }

    public OrganizationDto createOrganization(String name) {
        OrganizationDto orgTopCoder = new OrganizationDto();
        orgTopCoder.setName(name);
        OrganizationDto entity = organizationClient.createIfAbsent(orgTopCoder).getBody();
        contextHolder.addOrganizationToContext(entity);
        return entity;
    }

    public Long getCurrTimeInMillSeconds() {
        return System.currentTimeMillis();
    }

    public String getCurrTimeAsString() {
        return String.valueOf(System.currentTimeMillis());
    }

    public ConnectorDto createConnector(String name, String version) {
        contextHolder.setTokenForAdmin();
        String connectorName = connectorName(name, version);
        CreateConnectorDto dto = new CreateConnectorDto(
                DEFAULT_CONNECTOR_TYPE,
                AuthorizationType.Token,
                connectorName,
                "test desciption",
                name,
                getCurrTimeInMillSeconds().toString(),
                version.toLowerCase(),
                "https://github.com/lever/postings-api/",
                "https://test.com",
                "https://test.com",
                "https://assets.themuse.com/uploaded/companies/773/small_logo.png",
                TEST_CONNECTOR_PROPERTIES
        );
        return connectorClient.create(dto).getBody();
    }

    private String connectorName(String name, String version) {
        return name + " " + version + " " + getCurrTimeInMillSeconds();
    }

    public ConnectionDto createConnectionForUser(UserDto user) {
        AccessKeyDto accessKey = createAccessKey(user.getId(), getCurrTimeAsString());
        ConnectorDto connector = findConnector(SLACK_CHANNEL_V1_ERN);
        return createConnection(getCurrTimeAsString(), connector, accessKey.getId());
    }

    protected ConnectorDto findConnector(String ern) {
        return connectorClient.findAll().getBody().stream()
                .filter(c -> Objects.equals(c.getErn(), ern))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + ern));
    }
}
