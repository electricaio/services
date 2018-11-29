package io.electrica.user.security;

import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.user.repository.AccessKeyRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

public class UserExpressionMethods extends CommonExpressionMethods {

    private final AccessKeyRepository accessKeyRepository;

    private UserExpressionMethods(Authentication authentication, AccessKeyRepository accessKeyRepository) {
        super(authentication);
        this.accessKeyRepository = accessKeyRepository;
    }

    public boolean isUserAccessKey(Long accessKeyId) {
        return accessKeyRepository.isUserAccessKeyWithIdExists(accessKeyId, getUserId());
    }

    /**
     * Component that register {@link UserExpressionMethods} factory in 'user' namespace.
     */
    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final AccessKeyRepository accessKeyRepository;

        public Factory(AccessKeyRepository accessKeyRepository) {
            this.accessKeyRepository = accessKeyRepository;
        }

        @Override
        public String getNamespace() {
            return "user";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new UserExpressionMethods(authentication, accessKeyRepository);
        }
    }

}
