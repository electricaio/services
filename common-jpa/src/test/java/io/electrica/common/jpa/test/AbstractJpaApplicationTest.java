package io.electrica.common.jpa.test;

import io.electrica.common.config.EnvironmentTypeConfig;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.test.context.ForAccessKeyTestExecutionListener;
import io.electrica.test.context.ForUserTestExecutionListener;
import io.electrica.test.context.IdentityContextTestHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.inject.Inject;
import java.util.Set;

@ActiveProfiles(EnvironmentTypeConfig.TEST_ENV_PROFILE_NAME)
@TestExecutionListeners({ForUserTestExecutionListener.class, ForAccessKeyTestExecutionListener.class})
@TestPropertySource(locations = "classpath:common-test.properties")
public abstract class AbstractJpaApplicationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    protected IdentityContextHolder identityContextHolder;

    protected void executeForUser(
            long userId,
            long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions,
            Runnable work
    ) {
        IdentityContextTestHelper.executeForUser(
                identityContextHolder,
                userId,
                organizationId,
                roles,
                permissions,
                work
        );
    }

    protected void executeForAccessKey(long userId, long accessKeyId, Runnable work) {
        IdentityContextTestHelper.executeForAccessKey(identityContextHolder, userId, accessKeyId, work);
    }

}
