package io.electrica.metric.webhook.invocation.rest;

import io.electrica.MetricServiceApplication;
import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForUser;
import io.electrica.user.feign.AccessKeyClient;
import io.electrica.webhook.feign.WebhookManagementClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.UUID;

import static io.electrica.test.context.ForUser.DEFAULT_USER_ID;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MetricServiceApplication.class)
public class WebhookInvocationControllerImplTest  extends AbstractJpaApplicationTest {
    private static final Long NOT_DEFAULT_USER_ID = DEFAULT_USER_ID + 1;
    private static final Long USER_ACCESS_KEY_ID = 100L;
    private static final Long NOT_USER_ACCESS_KEY_ID = 101L;
    private static final UUID USER_WEBHOOK_ID = UUID.randomUUID();
    private static final UUID NOT_USER_WEBHOOK_ID = UUID.randomUUID();

    @Inject
    @MockBean
    private AccessKeyClient accessKeyClient;
    @Inject
    @MockBean
    private WebhookManagementClient webhookManagementClient;
    @Inject
    private WebhookInvocationControllerImpl controller;

    @Before
    public void init() {
        when(accessKeyClient.validateMyAccessKeyById(USER_ACCESS_KEY_ID))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));
        when(accessKeyClient.validateMyAccessKeyById(NOT_USER_ACCESS_KEY_ID))
                .thenReturn(ResponseEntity.ok(Boolean.FALSE));

        when(webhookManagementClient.webhookBelongsCurrentUser(USER_WEBHOOK_ID))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));
        when(webhookManagementClient.webhookBelongsCurrentUser(NOT_USER_WEBHOOK_ID))
                .thenReturn(ResponseEntity.ok(Boolean.FALSE));
    }

    @Test
    @ForUser(roles = RoleType.SuperAdmin, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForSuperAdmin() {
        controller.getWebhookInvocations(Pageable.unpaged(), null, null, null,
                null, null, null, null, null);
    }

    @Test
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForUserByUserId() {
        controller.getWebhookInvocations(Pageable.unpaged(), DEFAULT_USER_ID, null, null,
                null, null, null, null, null);
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForUserByNotUserId() {
        controller.getWebhookInvocations(Pageable.unpaged(), NOT_DEFAULT_USER_ID, null,
                null, null, null, null, null, null);
    }

    @Test
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForUserByUserAccessKeyId() {
        controller.getWebhookInvocations(Pageable.unpaged(), null, null, USER_ACCESS_KEY_ID,
                null, null, null, null, null);
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForUserByNotUserAccessKeyId() {
        controller.getWebhookInvocations(Pageable.unpaged(), null, null,
                NOT_USER_ACCESS_KEY_ID, null, null, null, null, null);
    }

    @Test
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForUserByUserWebhookId() {
        controller.getWebhookInvocations(Pageable.unpaged(), null, null, null,
                USER_WEBHOOK_ID, null, null, null, null);
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadWebhookInvocation)
    public void testPermissionsForUserByNotUserWebhookId() {
        controller.getWebhookInvocations(Pageable.unpaged(), null, null,
                null, NOT_USER_WEBHOOK_ID, null, null, null, null);
    }
}
