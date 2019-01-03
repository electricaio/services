package io.electrica.it.webhooks;

import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.UserDto;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebhookServiceTest extends BaseIT {

    private static final String WEBHOOK_PREFIX = "webhook-";
    private static final Map<String, String> TEST_WEBHOOK_PROPERTIES = Collections.singletonMap("a", "b");
    private static UserDto user;

    public void setUp() {
        createOrganization(ORG_HACKER_RANK);
        user = createUser(ORG_HACKER_RANK, RoleType.OrgUser);
        ConnectorDto connectorDto = createConnector(WEBHOOK_PREFIX + getCurrTimeAsString(), "1");
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKeyDto = createAccessKey(user.getId(), WEBHOOK_PREFIX + getCurrTimeAsString());
        createConnection("webhookTest", connectorDto, accessKeyDto.getId());
    }

    @BeforeEach
    public void setContext() {
        if (user == null) {
            setUp();
        }
        contextHolder.setContextForUser(user.getEmail());
    }

    @Test
    public void testAddWebhook() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        createAndSaveWebhook(accessKey.getId());
    }

    @Test
    public void testGetByConnection() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        ConnectionWebhookDto webhookDto = webhookClient.getByConnection(connectionDtos.getId()).getBody().get(0);
        assertNotNull(webhookDto);
    }

    @Test
    public void testDeleteWebhook() {
        AccessKeyDto accessKey = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        ConnectionWebhookDto dto = createAndSaveWebhook(accessKey.getId());
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKey.getId()).getBody().get(0);
        int count = webhookClient.getByConnection(connectionDtos.getId()).getBody().size();
        webhookClient.delete(dto.getId());
        int countAfterDeletion = webhookClient.getByConnection(connectionDtos.getId()).getBody().size();
        assertEquals(count - 1, countAfterDeletion);
    }

    private ConnectionWebhookDto createAndSaveWebhook(Long accessKeyId) {
        ConnectionDto connectionDtos = connectionClient.findAllByAccessKeyId(accessKeyId).getBody().get(0);
        assertNotNull(connectionDtos);
        ConnectionCreateWebhookDto webhookDto = new ConnectionCreateWebhookDto();
        webhookDto.setAccessKeyId(accessKeyId);
        webhookDto.setConnectionId(connectionDtos.getId());
        webhookDto.setName(WEBHOOK_PREFIX + getCurrTimeInMillSeconds());
        webhookDto.setProperties(TEST_WEBHOOK_PROPERTIES);
        ConnectionWebhookDto response = webhookClient.createConnection(webhookDto).getBody();
        assertNotNull(response.getId());
        assertNotNull(response.getUrl());
        assertEquals(webhookDto.getName(), response.getName());
        assertEquals(webhookDto.getAccessKeyId(), response.getAccessKeyId());
        assertEquals(webhookDto.getConnectionId(), response.getConnectionId());
        return response;
    }

}
