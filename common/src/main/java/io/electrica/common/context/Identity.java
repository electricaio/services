package io.electrica.common.context;

import io.electrica.common.security.PermissionType;
import org.springframework.security.core.Authentication;

import java.util.Set;

/**
 * Adds to {@link Authentication} some useful extra staff.
 */
public interface Identity {

    long NOT_AUTHENTICATED_USER_ID = -1;

    /**
     * Interface stub for not authenticated users.
     */
    Identity ANONYMOUS = () -> NOT_AUTHENTICATED_USER_ID;

    long getUserId();

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

    default Set<PermissionType> getPermissions() {
        throw new UnsupportedOperationException();
    }

    default long getOrganizationId() {
        throw new UnsupportedOperationException();
    }

}
