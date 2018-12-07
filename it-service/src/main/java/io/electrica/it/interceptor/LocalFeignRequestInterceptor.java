package io.electrica.it.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.electrica.it.context.ContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalFeignRequestInterceptor implements RequestInterceptor {

    private final ContextHolder contextHolder;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    public LocalFeignRequestInterceptor(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    @Override
    public synchronized void apply(RequestTemplate requestTemplate) {
        String accessToken = contextHolder.getAccessToken();
        if (accessToken != null) {
            requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, accessToken));
        }
    }
}
