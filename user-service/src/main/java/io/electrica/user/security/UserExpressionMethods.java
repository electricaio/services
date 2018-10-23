package io.electrica.user.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.user.service.AccessKeyDtoService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

public class UserExpressionMethods {

    private final Authentication authentication;
    private final MethodInvocation mi;
    private final AccessKeyDtoService accessKeyDtoService;

    public UserExpressionMethods(Authentication authentication, MethodInvocation mi,
                                 AccessKeyDtoService accessKeyDtoService) {
        this.authentication = authentication;
        this.mi = mi;
        this.accessKeyDtoService = accessKeyDtoService;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public boolean isUserAccessKey(Long accessKeyId) {
        return accessKeyDtoService.findByKey(accessKeyId).getUserId().longValue() == getIdentity().getUserId();
    }

    /**
     * Component that register {@link CommonExpressionMethods} factory in 'user' namespace.
     */
    @Component
    public static class Factory implements ExpressionMethodsFactory {

        @Inject
        private AccessKeyDtoService accessKeyDtoService;

        @Override
        public String getNamespace() {
            return "user";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new UserExpressionMethods(authentication, mi, accessKeyDtoService);
        }
    }


}
