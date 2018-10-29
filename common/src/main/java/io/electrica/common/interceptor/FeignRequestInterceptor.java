package io.electrica.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        Identity identity = IdentityContextHolder.getInstance().getIdentity();
        if (identity != null)
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, identity.getToken()));
    }
}
