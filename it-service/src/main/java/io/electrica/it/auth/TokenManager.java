package io.electrica.it.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    private static final MediaType MEDIA_TYPE_FORM_URL = MediaType.get("application/x-www-form-urlencoded");

    private final String clientId;
    private final String clientSecret;
    private final String accessTokenUri;
    private final ObjectMapper mapper;

    public TokenManager(@Value("${it-service.oauth2.client-id}") String clientId,
                        @Value("${it-service.oauth2.client-secret}") String clientSecret,
                        @Value("${it-service.oauth2.access-token-uri}") String accessTokenUri,
                        ObjectMapper mapper) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessTokenUri = accessTokenUri;
        this.mapper = mapper;
    }

    public TokenDetails getTokenDetailsForUser(String userName, String password) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_FORM_URL,
                "grant_type=password&username=@e:" + userName + "&password=" + password);
        return getTokenFromAuthServer(body);
    }

    public TokenDetails getNewAccessTokenFromRefreshToken(TokenDetails td) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_FORM_URL,
                "grant_type=refresh_token&refresh_token=" + td.getRefreshToken());
        return getTokenFromAuthServer(body);
    }

    private TokenDetails getTokenFromAuthServer(RequestBody body) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(clientId, clientSecret))
                .addHeader("Content-Type", MEDIA_TYPE_FORM_URL.toString())
                .url(accessTokenUri)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return parseToken(result);
        } catch (Exception e) {
            throw new RuntimeException("Error in getting token:" + e.getMessage());
        }
    }

    private TokenDetails parseToken(String json) throws Exception {
        return mapper.readValue(json, TokenDetails.class);
    }
}
