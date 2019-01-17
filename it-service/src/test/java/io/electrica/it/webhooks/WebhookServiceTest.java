package io.electrica.it.webhooks;

import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class WebhookServiceTest extends BaseIT {

    private static final String WEBHOOK_PREFIX = "webhook-";
    private static final Map<String, String> TEST_WEBHOOK_PROPERTIES = Collections.singletonMap("a", "b");
    private static final String ECHO_CONNECTOR_ERN = "ern://echo:test:1";
    private UserDto user;

    private static void assertConnectionWebhook(ConnectionCreateWebhookDto webhookDto, ConnectionWebhookDto response) {
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(webhookDto.getName(), response.getName());
        assertEquals(webhookDto.getAccessKeyId(), response.getAccessKeyId());
        assertEquals(webhookDto.getConnectionId(), response.getConnectionId());
        assertEquals(webhookDto.getIsPublic(), response.getIsPublic());

        assertNotNull(response.getSubmitUrl());
        assertNotNull(response.getInvokeUrl());
        Assert.assertEquals(webhookDto.getIsPublic(), response.getPublicSubmitUrl() != null);
        Assert.assertEquals(webhookDto.getIsPublic(), response.getPublicInvokeUrl() != null);
    }

    @BeforeAll
    public void setUp() {
        init();
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        contextHolder.setTokenForUser(user.getEmail());
        AccessKeyDto accessKey = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        ConnectorDto connectorDto = findConnector(ECHO_CONNECTOR_ERN);
        createConnection(WEBHOOK_PREFIX + getCurrTimeAsString(), connectorDto, accessKey.getId());
    }

    @Test
    public void testAddWebhook() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        createConnectionWebhook(accessKey.getId());
    }

    @Test
    public void testGetByConnection() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        ConnectionWebhookDto webhook = webhookManagementClient.getByConnection(connectionDtos.getId()).getBody().get(0);
        assertNotNull(webhook);
    }

    @Test
    public void testDeleteWebhook() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionWebhookDto dto = createConnectionWebhook(accessKey.getId());
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        int count = webhookManagementClient.getByConnection(connectionDtos.getId()).getBody().size();
        webhookManagementClient.delete(dto.getId());
        int countAfterDeletion = webhookManagementClient.getByConnection(connectionDtos.getId()).getBody().size();
        assertEquals(count - 1, countAfterDeletion);
    }

    private ConnectionWebhookDto createConnectionWebhook(Long accessKeyId) {
        ConnectionDto connectionDto = connectionClient.findAllByAccessKeyId(accessKeyId).getBody().get(0);
        assertNotNull(connectionDto);
        return createConnectionWebhook(connectionDto.getId(), accessKeyId);
    }

    private ConnectionWebhookDto createConnectionWebhook(Long connectionId, Long accessKeyId) {
        ConnectionCreateWebhookDto webhookDto = new ConnectionCreateWebhookDto();
        webhookDto.setAccessKeyId(accessKeyId);
        webhookDto.setConnectionId(connectionId);
        webhookDto.setName(WEBHOOK_PREFIX + getCurrTimeInMillSeconds());
        webhookDto.setIsPublic(true);
        webhookDto.setProperties(TEST_WEBHOOK_PROPERTIES);
        ConnectionWebhookDto response = webhookManagementClient.createConnection(webhookDto).getBody();
        assertConnectionWebhook(webhookDto, response);
        return response;
    }
}
