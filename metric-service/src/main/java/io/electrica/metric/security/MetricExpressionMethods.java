package io.electrica.metric.security;

import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.common.security.PermissionType;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

public class MetricExpressionMethods extends CommonExpressionMethods {

    public MetricExpressionMethods(Authentication authentication) {
        super(authentication);
    }

    public boolean canReadInstanceSession(Long userId) {
        if (userId != null) {
            return getPermissions().contains(PermissionType.ReadInstanceSession)
                    && getUserId() == userId;
        }
        return false;
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        @Override
        public String getNamespace() {
            return "metric";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new MetricExpressionMethods(authentication);
        }
    }
}
