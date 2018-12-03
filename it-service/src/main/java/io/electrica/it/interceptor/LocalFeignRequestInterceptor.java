package io.electrica.it.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class LocalFeignRequestInterceptor implements RequestInterceptor {


    private final String clientId;
    private final String clientSecret;
    private final String accessTokenUri;

    private static final Logger log = LoggerFactory.getLogger(LocalFeignRequestInterceptor.class);
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    private static final AtomicReference<String> token = new AtomicReference<>();

    public LocalFeignRequestInterceptor(@Value("${security.oauth2.client-id}") String clientId,
                                        @Value("${security.oauth2.client-secret}") String clientSecret,
                                        @Value("${security.oauth2.access-token-uri}") String accessTokenUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessTokenUri = accessTokenUri;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.info("Applying request template:%s", requestTemplate.toString());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            token.set(getAccessToken());
        }
        requestTemplate.header("Authorization", token.get());
    }

    private String getAccessToken() {
        String encodedString = new String(Base64.getEncoder().encode((clientId + ":" + clientSecret)
                .getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"));

        RequestBody body = RequestBody.create(MediaType.get(CONTENT_TYPE_FORM),
                "grant_type=password&username=@e:admin@electrica.io&password=admin");

        return getTokenFromAuthServer(encodedString, body);
    }

    private String getTokenFromAuthServer(String encodedString, RequestBody body) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedString)
                .addHeader("Content-Type", CONTENT_TYPE_FORM)
                .url(accessTokenUri)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        String result;
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            log.debug("Response :%s", result);
        } catch (IOException e) {
            throw new RuntimeException("Error in getting token:" + e.getMessage());
        }

        return (String) parseJson(result).get("access_token");
    }

    private Map<String, Object> parseJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = null;

        try {
            result = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("Problem parsing json response", e);
        }
        return result;
    }
}
