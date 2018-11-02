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
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Collect common things to work with JWT tokens.
 */
public class TokenHelper {

    public static final String JWT_TOKEN_IDENTIFIER = "jti";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ID_INDICATOR = "@id:";
    private static final String EMAIL_INDICATOR = "@e:";
    private static final String ISSUED_AT_ID = "iat";

    private TokenHelper() {
    }


    public static String buildIdTokenUsername(Long userId) {
        requireNonNull(userId, "userId");
        return ID_INDICATOR + userId;
    }

    public static boolean isIdTokenUsername(String username) {
        return username.startsWith(ID_INDICATOR);
    }

    public static long extractIdFromTokenUsername(String username) {
        if (isIdTokenUsername(username)) {
            return Long.parseLong(username.substring(ID_INDICATOR.length()));
        }
        throw new IllegalArgumentException("wrong token username: " + username);
    }


    public static boolean isEmailTokenUsername(String username) {
        return username.startsWith(EMAIL_INDICATOR);
    }

    public static String extractEmailFromTokenUsername(String username) {
        if (isEmailTokenUsername(username)) {
            return StringUtils.substringAfter(username, EMAIL_INDICATOR);
        }
        throw new IllegalArgumentException("wrong token username: " + username);
    }


    public static OAuth2AccessToken addIssuedAtInfo(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        result.getAdditionalInformation().put(ISSUED_AT_ID, Instant.now().getEpochSecond());
        return result;
    }

    public static long getIssuedAt(String token) {
        return getIssuedAt(getClaims(token));
    }

    public static long getIssuedAt(Map<String, String> claims) {
        String iat = claims.get(ISSUED_AT_ID);
        if (iat == null) {
            throw new IllegalStateException(ISSUED_AT_ID + " absent in token");
        }
        return Long.parseLong(iat);
    }

    public static UUID getJti(Map<String, String> claims) {
        String jti = claims.get(JWT_TOKEN_IDENTIFIER);
        if (jti == null) {
            throw new IllegalStateException(JWT_TOKEN_IDENTIFIER + " absent in token");
        }
        return UUID.fromString(jti);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static Map<String, String> getClaims(String token) {
        Jwt jwt = JwtHelper.decode(token);
        return OBJECT_MAPPER.readValue(jwt.getClaims(), Map.class);
    }

}
