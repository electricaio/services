package io.electrica.test.context;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ForUserTestExecutionListener extends AbstractTestExecutionListener {

    private IdentityContextHolder identityContextHolder;

    @Override
    public void beforeTestClass(TestContext testContext) {
        identityContextHolder = testContext.getApplicationContext().getBean(IdentityContextHolder.class);
    }

    @Override
    public void beforeTestExecution(TestContext testContext) {
        ForUser annotation = testContext.getTestMethod().getAnnotation(ForUser.class);
        if (annotation != null) {
            Identity identity = IdentityContextTestHelper.createIdentity(
                    annotation.userId(),
                    annotation.organizationId(),
                    Arrays.stream(annotation.permissions()).collect(Collectors.toSet())
            );
            identityContextHolder.setIdentity(identity);
        }
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        if (testContext.getTestMethod().isAnnotationPresent(ForUser.class)) {
            identityContextHolder.clearIdentity();
        }
    }
}
