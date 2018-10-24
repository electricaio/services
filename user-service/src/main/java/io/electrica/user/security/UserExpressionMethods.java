package io.electrica.user.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.user.repository.AccessKeyRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

public class UserExpressionMethods {

    private final Authentication authentication;
    private final MethodInvocation mi;
    private final AccessKeyRepository accessKeyRepository;

    public UserExpressionMethods(Authentication authentication, MethodInvocation mi,
                                 AccessKeyRepository accessKeyRepository) {
        this.authentication = authentication;
        this.mi = mi;
        this.accessKeyRepository = accessKeyRepository;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public boolean isUserAccessKey(Long accessKeyId) {
        return accessKeyRepository.exists(accessKeyId,getIdentity().getUserId());
    }

    /**
     * Component that register {@link UserExpressionMethods} factory in 'user' namespace.
     */
    @Component
    public static class Factory implements ExpressionMethodsFactory {

        @Inject
        private AccessKeyRepository accessKeyRepository;

        @Override
        public String getNamespace() {
            return "user";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new UserExpressionMethods(authentication, mi, accessKeyRepository);
        }
    }


}
