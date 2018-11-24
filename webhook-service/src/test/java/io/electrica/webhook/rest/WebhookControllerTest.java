package io.electrica.webhook.rest;

import com.google.common.collect.Sets;
import io.electrica.WebhookServiceApplicationTest;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
import io.electrica.webhook.repository.WebhookRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class WebhookControllerTest extends WebhookServiceApplicationTest {

    @Inject
    private WebhookControllerImpl webhookController;

    @MockBean
    @Inject
    private ConnectionClient connectionClient;

    @Before
    public void setup() {
    }

    @Test
    public void testWebhookCreate() {
        CreateWebhookDto createWebhookDto = createWebhookDto("test");
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    WebhookDto actual = webhookController.create(createWebhookDto).getBody();
                    assertEquals(createWebhookDto.getName(), actual.getName());
                    assertEquals(createWebhookDto.getConnectionId(), actual.getConnectionId());
                    assertEquals(createWebhookDto.getConnectorId(), actual.getConnectorId());
                    assertEquals(createWebhookDto.getOrganizationId(), actual.getOrganizationId());
                    Assert.assertNotNull(actual.getUrl());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testWebhookWithUserHasNoCreateWebhookPermission() {
        CreateWebhookDto createWebhookDto = WebhookDto.builder()
                .connectionId(1L)
                .connectorId(1L)
                .name("test")
                .userId(1L)
                .organizationId(1L).build();
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.SuperAdmin), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    WebhookDto actual = webhookController.create(createWebhookDto).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testWebhookWithValidateConnectionFailed() {
        CreateWebhookDto createWebhookDto = WebhookDto.builder()
                .connectionId(1L)
                .connectorId(1L)
                .name("test")
                .userId(1L)
                .organizationId(1L).build();
        doReturn(ResponseEntity.ok(false)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.SuperAdmin), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    WebhookDto actual = webhookController.create(createWebhookDto).getBody();
                });
    }

    private CreateWebhookDto createWebhookDto(String name) {
        return WebhookDto.builder()
                .connectionId(1L)
                .connectorId(1L)
                .name(name)
                .userId(1L)
                .organizationId(1L).build();
    }
}
