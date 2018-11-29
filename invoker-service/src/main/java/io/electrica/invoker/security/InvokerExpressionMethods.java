package io.electrica.invoker.security;

import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.user.feign.AccessKeyClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static io.electrica.common.helper.CollectionUtils.nullToFalse;

public class InvokerExpressionMethods extends CommonExpressionMethods {

    private final AccessKeyClient accessKeyClient;

    private InvokerExpressionMethods(Authentication authentication, AccessKeyClient accessKeyClient) {
        super(authentication);
        this.accessKeyClient = accessKeyClient;
    }

    public boolean validateAccessKey() {
        Boolean result = accessKeyClient.validateMyAccessKey().getBody();
        return nullToFalse(result);
    }

    /**
     * Component that register {@link InvokerExpressionMethods} factory in 'invoker' namespace.
     */
    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final AccessKeyClient accessKeyClient;

        public Factory(AccessKeyClient accessKeyClient) {
            this.accessKeyClient = accessKeyClient;
        }

        @Override
        public String getNamespace() {
            return "invoker";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new InvokerExpressionMethods(authentication, accessKeyClient);
        }
    }

}
