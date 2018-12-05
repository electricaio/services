package io.electrica.it.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.electrica.it.context.SessionContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public synchronized void apply(RequestTemplate requestTemplate) {
        String accessToken = SessionContextHolder.getInstance().getAccessToken();
        if (accessToken != null) {
            requestTemplate.header("Authorization", accessToken);
        }
    }
}
