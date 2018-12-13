package io.electrica.webhook.rest;

import com.google.common.collect.Sets;
import io.electrica.WebhookServiceApplicationTest;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.user.feign.AccessKeyClient;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

public class WebhookControllerTest extends WebhookServiceApplicationTest {

    @Inject
    private WebhookControllerImpl webhookController;

    @MockBean
    @Inject
    private ConnectionClient connectionClient;

    @MockBean
    @Inject
    private AccessKeyClient accessKeyClient;

    private static ConnectionCreateWebhookDto connectionCreateWebhookDto(
            String name,
            Long accessKeyId,
            Long connectionId
    ) {
        ConnectionCreateWebhookDto dto = new ConnectionCreateWebhookDto();
        dto.setName(name);
        dto.setAccessKeyId(accessKeyId);
        dto.setConnectionId(connectionId);
        return dto;
    }

    @Before
    public void setup() {
    }

    @Test
    public void connectionCreateTest() {
        Long userId = 1L;
        Long organizationId = 1L;
        Long accessKeyId = 1L;
        Long connectionId = 1L;
        Map<String, String> properties = Collections.singletonMap("a", "b");

        ConnectionCreateWebhookDto dto = connectionCreateWebhookDto("test", accessKeyId, connectionId);
        dto.setProperties(properties);

        mockConnectionClient(accessKeyId, connectionId, true);

        executeForUser(
                userId,
                organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook),
                () -> {
                    ConnectionWebhookDto actual = webhookController.createConnection(dto).getBody();
                    assertEquals(dto.getName(), actual.getName());
                    assertEquals(dto.getAccessKeyId(), actual.getAccessKeyId());
                    assertEquals(dto.getConnectionId(), actual.getConnectionId());
                    assertEquals(organizationId, actual.getOrganizationId());
                    assertEquals(userId, actual.getUserId());
                    assertEquals(properties, actual.getProperties());
                    assertNotNull(actual.getUrl());
                }
        );
    }

    private void mockConnectionClient(Long accessKeyId, Long connectionId, boolean connectionBelongsCurrentUser) {
        doReturn(ResponseEntity.ok(connectionBelongsCurrentUser))
                .when(connectionClient)
                .connectionBelongsCurrentUser(connectionId);

        ConnectionDto connection = new ConnectionDto();
        connection.setAccessKeyId(accessKeyId);
        doReturn(ResponseEntity.ok(connection))
                .when(connectionClient)
                .get(connectionId);

        doReturn(ResponseEntity.ok(true)).when(accessKeyClient).validateMyAccessKey();
    }

    @Test(expected = AccessDeniedException.class)
    public void testWebhookWithUserHasNoCreateWebhookPermission() {
        Long userId = 1L;
        Long organizationId = 1L;
        Long accessKeyId = 1L;
        Long connectionId = 1L;

        ConnectionCreateWebhookDto dto = connectionCreateWebhookDto("test", accessKeyId, connectionId);

        mockConnectionClient(accessKeyId, connectionId, true);

        executeForUser(
                userId,
                organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook),
                () -> webhookController.createConnection(dto)
        );
    }

    @Test(expected = AccessDeniedException.class)
    public void testWebhookWithValidateConnectionFailed() {
        Long userId = 1L;
        Long organizationId = 1L;
        Long accessKeyId = 1L;
        Long connectionId = 1L;

        ConnectionCreateWebhookDto dto = connectionCreateWebhookDto("test", accessKeyId, connectionId);

        mockConnectionClient(accessKeyId, connectionId, false);

        executeForUser(
                userId,
                organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook),
                () -> webhookController.createConnection(dto)
        );
    }

    @Test
    public void testGetByConnection() {
        Long userId = 1L;
        Long organizationId = 1L;
        Long accessKeyId = 1L;
        Long connectionId = 1L;

        ConnectionCreateWebhookDto dto1 = connectionCreateWebhookDto("test1", accessKeyId, connectionId);
        ConnectionCreateWebhookDto dto2 = connectionCreateWebhookDto("test2", accessKeyId, connectionId);
        ConnectionCreateWebhookDto dto3 = connectionCreateWebhookDto("test3", accessKeyId, connectionId);

        mockConnectionClient(accessKeyId, connectionId, true);

        executeForUser(
                userId,
                organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook),
                () -> {
                    webhookController.createConnection(dto1);
                    webhookController.createConnection(dto2);
                    webhookController.createConnection(dto3);
                }
        );
        flushAndClear();

        executeForUser(
                userId,
                organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    List<ConnectionWebhookDto> webhooks = webhookController.getByConnection(connectionId).getBody();
                    assertEquals(3, webhooks.size());
                    assertEquals(dto1.getName(), webhooks.get(0).getName());
                    assertEquals(dto2.getName(), webhooks.get(1).getName());
                    assertEquals(dto3.getName(), webhooks.get(2).getName());
                });
    }

    @Test
    public void testDeleteWebhook() {
        Long userId = 1L;
        Long organizationId = 1L;
        Long accessKeyId = 1L;
        Long connectionId = 1L;

        ConnectionCreateWebhookDto dto1 = connectionCreateWebhookDto("test1", accessKeyId, connectionId);

        mockConnectionClient(accessKeyId, connectionId, true);

        executeForUser(userId, organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook),
                () -> webhookController.createConnection(dto1)
        );
        flushAndClear();

        AtomicReference<UUID> webhookId = new AtomicReference<>();
        executeForUser(userId, organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    List<ConnectionWebhookDto> webhooks = webhookController.getByConnection(connectionId).getBody();
                    assertEquals(1, webhooks.size());
                    webhookId.set(webhooks.get(0).getId());
                });

        executeForUser(userId, organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.DeleteWebhook), () -> {
                    webhookController.delete(webhookId.get());
                });
        flushAndClear();

        executeForUser(userId, organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    List<ConnectionWebhookDto> webhooks = webhookController.getByConnection(connectionId).getBody();
                    assertEquals(0, webhooks.size());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeleteWebhookWithoutPermission() {
        Long userId = 1L;
        Long organizationId = 1L;
        Long accessKeyId = 1L;
        Long connectionId = 1L;

        ConnectionCreateWebhookDto dto1 = connectionCreateWebhookDto("test1", accessKeyId, connectionId);

        mockConnectionClient(accessKeyId, connectionId, true);

        AtomicReference<UUID> webhookId = new AtomicReference<>();
        executeForUser(userId, organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook),
                () -> webhookId.set(webhookController.createConnection(dto1).getBody().getId())
        );
        flushAndClear();

        executeForUser(userId, organizationId,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.DeleteWebhook), () -> {
                    webhookController.delete(webhookId.get());
                });
    }

}
