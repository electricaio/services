package io.electrica.test;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.security.PermissionType;
import io.electrica.test.context.ForUserTestExecutionListener;
import io.electrica.test.context.IdentityContextTestHelper;
import lombok.SneakyThrows;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.inject.Inject;
import java.util.Set;

@ActiveProfiles("test")
@TestExecutionListeners(ForUserTestExecutionListener.class)
@TestPropertySource(locations = "classpath:common-test.properties")
public abstract class AbstractJpaApplicationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    protected IdentityContextHolder identityContextHolder;

    @SneakyThrows
    protected void executeForUser(long userId, long organizationId, Set<PermissionType> permissions, Runnable work) {
        Identity identity = IdentityContextTestHelper.createIdentity(userId, organizationId, permissions);
        identityContextHolder.executeWithContext(identity, work::run);
    }

}
