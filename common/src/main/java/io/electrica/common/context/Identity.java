package io.electrica.common.context;

import org.springframework.security.core.Authentication;

/**
 * Adds to {@link Authentication} some useful extra staff.
 */
public interface Identity {

    int NOT_AUTHENTICATED_USER_ID = -1;

    /**
     * Interface stub for not authenticated users.
     */
    Identity ANONYMOUS = () -> NOT_AUTHENTICATED_USER_ID;

    int getUserId();

    default boolean isAuthenticated() {
        return false;
    }

    default String getToken() {
        throw new UnsupportedOperationException();
    }

    default long getTokenIssuedAt() {
        throw new UnsupportedOperationException();
    }

    default Authentication getAuthentication() {
        throw new UnsupportedOperationException();
    }

}
