package io.electrica.it.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
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
        String accessToken = contextHolder.getAccessToken();
        if (accessToken != null) {
            requestTemplate.header("Authorization", accessToken);
        }
    }
}
