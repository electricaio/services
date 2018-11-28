package io.electrica.webhook.rest;

import com.google.common.collect.Sets;
import io.electrica.WebhookServiceApplicationTest;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
        CreateWebhookDto createWebhookDto = new CreateWebhookDto("test", 1L, 1L, 1L, 1L);
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto.getConnectionId());

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    WebhookDto actual = webhookController.create(createWebhookDto).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testWebhookWithValidateConnectionFailed() {
        CreateWebhookDto createWebhookDto = new CreateWebhookDto("test", 1L, 1L, 1L, 1L);
        doReturn(ResponseEntity.ok(false)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    WebhookDto actual = webhookController.create(createWebhookDto).getBody();
                });
    }

    @Test
    public void testGetByConnection() {
        CreateWebhookDto createWebhookDto1 = createWebhookDto("test1");
        CreateWebhookDto createWebhookDto2 = createWebhookDto("test2");
        CreateWebhookDto createWebhookDto3 = createWebhookDto("test3");
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto1.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    webhookController.create(createWebhookDto1);
                    webhookController.create(createWebhookDto2);
                    webhookController.create(createWebhookDto3);
                });
        flushAndClear();

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    List<WebhookDto> webhooks = webhookController.getByConnection(1L).getBody();
                    assertEquals(3, webhooks.size());
                    assertEquals(createWebhookDto1.getName(), webhooks.get(0).getName());
                    assertEquals(createWebhookDto2.getName(), webhooks.get(1).getName());
                    assertEquals(createWebhookDto3.getName(), webhooks.get(2).getName());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetByConnectionWithWebhookRepository() {
        executeForUser(2, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    List<WebhookDto> webhooks = webhookController.getByConnection(1L).getBody();
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetByConnectionWithConnectionBelongToDiffUser() {
        CreateWebhookDto createWebhookDto1 = createWebhookDto("test1");

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    webhookController.create(createWebhookDto1);
                });

        executeForUser(2, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    webhookController.getByConnection(1L).getBody();
                });
    }


    @Test
    public void testGetByUUID() {
        CreateWebhookDto createWebhookDto = createWebhookDto("test");
        CreateWebhookDto createWebhookDto2 = createWebhookDto("test2");
        AtomicReference<WebhookDto> webhookDtoReference = new AtomicReference<>();
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    webhookDtoReference.set(webhookController.create(createWebhookDto).getBody());
                    webhookController.create(createWebhookDto2);

                });

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    WebhookDto webhookDto = webhookController.getById(getUUIDFromWebhook(
                            webhookDtoReference.get().getUrl())).getBody();
                    assertEquals(createWebhookDto.getName(), webhookDto.getName());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetByUUIDConnectWithDiffUser() {
        CreateWebhookDto createWebhookDto = createWebhookDto("test");
        CreateWebhookDto createWebhookDto2 = createWebhookDto("test2");
        AtomicReference<WebhookDto> webhookDtoReference = new AtomicReference<>();
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    webhookDtoReference.set(webhookController.create(createWebhookDto).getBody());
                    webhookController.create(createWebhookDto2);

                });
        executeForUser(2, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.ReadWebhook), () -> {
                    WebhookDto webhookDto = webhookController.getById(getUUIDFromWebhook(
                            webhookDtoReference.get().getUrl())).getBody();
                    assertEquals(createWebhookDto.getName(), webhookDto.getName());
                });
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetByUUIDConnectWithoutReadPermission() {
        CreateWebhookDto createWebhookDto = createWebhookDto("test");
        CreateWebhookDto createWebhookDto2 = createWebhookDto("test2");
        AtomicReference<WebhookDto> webhookDtoReference = new AtomicReference<>();
        doReturn(ResponseEntity.ok(true)).when(connectionClient).validate(createWebhookDto.getConnectionId());
        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    webhookDtoReference.set(webhookController.create(createWebhookDto).getBody());
                    webhookController.create(createWebhookDto2);

                });

        executeForUser(1, 1,
                Sets.newHashSet(RoleType.OrgUser), Sets.newHashSet(PermissionType.CreateWebhook), () -> {
                    WebhookDto webhookDto = webhookController.getById(getUUIDFromWebhook(
                            webhookDtoReference.get().getUrl())).getBody();
                    assertEquals(createWebhookDto.getName(), webhookDto.getName());
                });
    }

    private UUID getUUIDFromWebhook(String url) {
        return UUID.fromString(url.split("/")[3]);
    }

    private CreateWebhookDto createWebhookDto(String name) {
        return new CreateWebhookDto(name, 1L, 1L, 1L, 1L);
    }
}
