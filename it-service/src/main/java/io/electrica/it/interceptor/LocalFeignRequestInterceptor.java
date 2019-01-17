package io.electrica.it.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.electrica.it.auth.TokenDetails;
import io.electrica.it.context.ContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalFeignRequestInterceptor implements RequestInterceptor {

    private final ContextHolder contextHolder;

    public LocalFeignRequestInterceptor(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    @Override
    public synchronized void apply(RequestTemplate requestTemplate) {
        TokenDetails token = contextHolder.getToken();
        if (token != null) {
            requestTemplate.header("Authorization", "Bearer " + token.getAccessToken());
        }
    }
}
