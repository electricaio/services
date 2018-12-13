package io.electrica.test.context;

import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.context.IdentityImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class ForAccessKeyTestExecutionListener extends AbstractTestExecutionListener {

    private IdentityContextHolder identityContextHolder;

    @Override
    public void beforeTestClass(TestContext testContext) {
        identityContextHolder = testContext.getApplicationContext().getBean(IdentityContextHolder.class);
    }

    @Override
    public void beforeTestExecution(TestContext testContext) {
        ForAccessKey annotation = testContext.getTestMethod().getAnnotation(ForAccessKey.class);
        if (annotation != null) {
            Authentication authentication = IdentityContextTestHelper.createAccessKeyAuthentication(
                    annotation.userId(),
                    annotation.accessKeyId(),
                    0L
            );
            IdentityImpl identity = new IdentityImpl(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            identityContextHolder.setIdentity(identity);
        }
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        if (testContext.getTestMethod().isAnnotationPresent(ForAccessKey.class)) {
            identityContextHolder.clearIdentity();
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }
}
