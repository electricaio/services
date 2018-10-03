package io.electrica.common.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

public class CommonExpressionMethods {

    private final Authentication authentication;
    private final MethodInvocation mi;

    private CommonExpressionMethods(Authentication authentication, MethodInvocation mi) {
        this.authentication = authentication;
        this.mi = mi;
    }

    public Identity getIdentity(){
        return new IdentityImpl(authentication);
    }

    public boolean isUser(Integer userId){
        return Objects.equals(getIdentity().getUserId(), userId);
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        @Override
        public String getId() {
            return "common";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new CommonExpressionMethods(authentication, mi);
        }
    }

}
