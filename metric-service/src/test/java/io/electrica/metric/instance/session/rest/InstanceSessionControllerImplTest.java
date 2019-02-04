package io.electrica.metric.instance.session.rest;

import io.electrica.MetricServiceApplication;
import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.test.context.ForUser;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.time.LocalDateTime;

@SpringBootTest(classes = MetricServiceApplication.class)
public class InstanceSessionControllerImplTest extends AbstractJpaApplicationTest {

    @Inject
    private InstanceSessionControllerImpl instanceSessionController;

    @Test
    @ForUser(roles = RoleType.SuperAdmin)
    public void testPermissionsForSuperAdmin() {
        instanceSessionController.getInstanceSessions(Pageable.unpaged(), LocalDateTime.now(), LocalDateTime.now(),
                "", new SessionState[]{SessionState.Running}, 1L, 2L, 3L);
    }

    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadUser)
    @Test(expected = AccessDeniedException.class)
    public void testNoPermissionsForUser() {
        instanceSessionController.getInstanceSessions(Pageable.unpaged(), LocalDateTime.now(), LocalDateTime.now(),
                null, null, null, 1L, null);
    }

    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadInstanceSession)
    @Test(expected = AccessDeniedException.class)
    public void testPermissionsForDifferentUser() {
        instanceSessionController.getInstanceSessions(Pageable.unpaged(), LocalDateTime.now(), LocalDateTime.now(),
                null, null, null, 2L, null);
    }

    @ForUser(roles = RoleType.OrgUser, permissions = PermissionType.ReadInstanceSession)
    @Test
    public void testPermissionsForUser() {
        instanceSessionController.getInstanceSessions(Pageable.unpaged(), LocalDateTime.now(), LocalDateTime.now(),
                null, null, null, 0L, null);
    }
}
