package io.electrica.metric.connection.invocation.rest;

import io.electrica.MetricServiceApplication;
import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.service.InstanceSessionService;
import io.electrica.test.context.ForUser;
import io.electrica.user.feign.AccessKeyClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import static io.electrica.test.context.ForUser.DEFAULT_USER_ID;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MetricServiceApplication.class)
public class ConnectionInvocationControllerImplTest extends AbstractJpaApplicationTest {
    private static final Long USER_ACCESS_KEY_ID = 100L;
    private static final Long NOT_USER_ACCESS_KEY_ID = 101L;
    private static final Long USER_CONNECTION_ID = 1000L;
    private static final Long NOT_USER_CONNECTION_ID = 1001L;
    private static final UUID USER_INSTANCE_ID = UUID.randomUUID();
    private static final UUID NOT_USER_INSTANCE_ID = UUID.randomUUID();

    @Inject
    @MockBean
    private InstanceSessionService instanceSessionService;
    @Inject
    @MockBean
    private AccessKeyClient accessKeyClient;
    @Inject
    @MockBean
    private ConnectionClient connectionClient;
    @Inject
    private ConnectionInvocationControllerImpl controller;

    @Before
    public void init() {
        when(accessKeyClient.validateMyAccessKeyById(USER_ACCESS_KEY_ID))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));
        when(accessKeyClient.validateMyAccessKeyById(NOT_USER_ACCESS_KEY_ID))
                .thenReturn(ResponseEntity.ok(Boolean.FALSE));

        when(connectionClient.connectionBelongsCurrentUser(USER_CONNECTION_ID))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));
        when(connectionClient.connectionBelongsCurrentUser(NOT_USER_CONNECTION_ID))
                .thenReturn(ResponseEntity.ok(Boolean.FALSE));

        InstanceSession userInstanceSession = new InstanceSession();
        userInstanceSession.setUserId(DEFAULT_USER_ID);
        when(instanceSessionService.findById(USER_INSTANCE_ID))
                .thenReturn(Optional.of(userInstanceSession));

        InstanceSession notUserInstanceSession = new InstanceSession();
        notUserInstanceSession.setUserId(DEFAULT_USER_ID + 1);
        when(instanceSessionService.findById(NOT_USER_INSTANCE_ID))
                .thenReturn(Optional.of(notUserInstanceSession));
    }

    @Test
    @ForUser(roles = RoleType.SuperAdmin, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForSuperAdmin() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, null,
                null, null, null, null, null, null);
    }

    @Test
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByUserId() {
        controller.getConnectionInvocations(Pageable.unpaged(), DEFAULT_USER_ID, null, null,
                null, null, null, EnumSet.allOf(ConnectionInvocationStatus.class),
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByNotUserId() {
        controller.getConnectionInvocations(Pageable.unpaged(), DEFAULT_USER_ID + 1, null,
                null, null, null, null,
                EnumSet.allOf(ConnectionInvocationStatus.class), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByUserAccessKeyId() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, USER_ACCESS_KEY_ID,
                null, null, null, EnumSet.allOf(ConnectionInvocationStatus.class),
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByNotUserAccessKeyId() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, NOT_USER_ACCESS_KEY_ID,
                null, null, null, EnumSet.allOf(ConnectionInvocationStatus.class),
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByUserConnectionId() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, null,
                null, USER_CONNECTION_ID, null, EnumSet.allOf(ConnectionInvocationStatus.class),
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByNotUserConnectionId() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, null,
                null, NOT_USER_CONNECTION_ID, null,
                EnumSet.allOf(ConnectionInvocationStatus.class), LocalDateTime.now(), LocalDateTime.now());
    }

    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByUserInstanceId() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, null,
                USER_INSTANCE_ID, null, null, EnumSet.allOf(ConnectionInvocationStatus.class),
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test(expected = AccessDeniedException.class)
    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadConnectionInvocation)
    public void testPermissionsForUserByNotInstanceId() {
        controller.getConnectionInvocations(Pageable.unpaged(), null, null, null,
                NOT_USER_INSTANCE_ID, null, null,
                EnumSet.allOf(ConnectionInvocationStatus.class), LocalDateTime.now(), LocalDateTime.now());
    }
}
