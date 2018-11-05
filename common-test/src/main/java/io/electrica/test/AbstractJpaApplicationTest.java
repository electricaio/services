package io.electrica.test;

import io.electrica.common.config.EnvironmentTypeConfig;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForAccessKeyTestExecutionListener;
import io.electrica.test.context.ForUserTestExecutionListener;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.inject.Inject;
import java.util.Set;

import static io.electrica.test.context.IdentityContextTestHelper.createAccessKeyAuthentication;
import static io.electrica.test.context.IdentityContextTestHelper.createAuthentication;

@ActiveProfiles(EnvironmentTypeConfig.TEST_ENV_PROFILE_NAME)
@TestExecutionListeners({ForUserTestExecutionListener.class, ForAccessKeyTestExecutionListener.class})
@TestPropertySource(locations = "classpath:common-test.properties")
public abstract class AbstractJpaApplicationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    protected IdentityContextHolder identityContextHolder;

    @SneakyThrows
    protected void executeForUser(
            long userId,
            long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions,
            Runnable work
    ) {
        Authentication authentication = createAuthentication(userId, organizationId, roles, permissions);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            IdentityImpl identity = new IdentityImpl(authentication);
            identityContextHolder.executeWithContext(identity, work::run);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

    }

    @SneakyThrows
    protected void executeForAccessKey(long userId, long accessKeyId, Runnable work) {
        Authentication authentication = createAccessKeyAuthentication(userId, accessKeyId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            IdentityImpl identity = new IdentityImpl(authentication);
            identityContextHolder.executeWithContext(identity, work::run);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

}
