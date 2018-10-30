package io.electrica.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/*
 * Feign requests interceptor is used for adding authorization headers to inter-service calls.
 * No guarantees are give with regards to the order that interceptors are applied.
 */

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Inject
    private IdentityContextHolder identityContextHolder;

    @Override
    public void apply(RequestTemplate template) {
        Identity identity = identityContextHolder.getIdentity();
        if (identity.isFeignRequest()) {
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, identity.getToken()));
        }
    }
}
