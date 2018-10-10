package io.electrica.common.helper;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.time.Instant;
import java.util.Map;

/**
 * Collect common things to work with JWT tokens.
 */
public class TokenHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String ID_INDICATOR = "@id:";
    private static final String EMAIL_INDICATOR = "@e:";

    private static final String ISSUED_AT_ID = "iat";

    private TokenHelper() {
    }

    public static String tokenUsernameFromId(long userId) {
        return ID_INDICATOR + userId;
    }

    public static int idFromTokenUsername(String username) {
        if (isId(username)) {
            return extractId(username);
        }
        throw new IllegalArgumentException("wrong token username: " + username);
    }

    public static boolean isId(String username) {
        return username.startsWith(ID_INDICATOR);
    }

    public static boolean isEmail(String username) {
        return username.startsWith(EMAIL_INDICATOR);
    }


    public static int extractId(String username) {
        return Integer.parseInt(username.substring(ID_INDICATOR.length()));
    }

    public static String extractEmail(String username) {
        return StringUtils.substringAfter(username, EMAIL_INDICATOR);
    }

    public static OAuth2AccessToken addIssuedAtInfo(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        result.getAdditionalInformation().put(ISSUED_AT_ID, Instant.now().getEpochSecond());
        return result;
    }

    @SneakyThrows
    public static long getIssuedAt(String token) {
        Jwt jwt = JwtHelper.decode(token);
        Object iat = OBJECT_MAPPER.readValue(jwt.getClaims(), Map.class).get(ISSUED_AT_ID);
        if (iat == null) {
            throw new IllegalStateException("iat absent in token");
        }
        return Long.parseLong(iat.toString());
    }

}
