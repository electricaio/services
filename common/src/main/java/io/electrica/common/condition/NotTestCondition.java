package io.electrica.common.condition;

import io.electrica.common.config.EnvironmentTypeConfig;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NotTestCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !context.getEnvironment().acceptsProfiles(EnvironmentTypeConfig.TEST_ENV_PROFILE_NAME);
    }
}
