package io.electrica.common.context;

import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.core.Authentication;

import java.util.Set;
import java.util.UUID;

/**
 * Adds to {@link Authentication} some useful extra staff.
 */
public interface Identity {

    long NOT_AUTHENTICATED_USER_ID = -1;
    long NOT_AUTHENTICATED_ORGANIZATION_ID = -1;

    /**
     * Interface stub for not authenticated users.
     */
    Identity ANONYMOUS = new Identity() {
        @Override
        public long getUserId() {
            return NOT_AUTHENTICATED_USER_ID;
        }

        @Override
        public long getOrganizationId() {
            return NOT_AUTHENTICATED_ORGANIZATION_ID;
        }
    };

    long getUserId();

    default boolean isAuthenticated() {
        return false;
    }

    default String getToken() {
        throw new UnsupportedOperationException();
    }

    default UUID getTokenJti() {
        throw new UnsupportedOperationException();
    }

    default Long getAccessKeyId() {
        throw new UnsupportedOperationException();
    }

    default Long getAccessKeyIdIfPresent() {
        return null;
    }

    default long getTokenIssuedAt() {
        throw new UnsupportedOperationException();
    }

    default Authentication getAuthentication() {
        throw new UnsupportedOperationException();
    }

    default Set<RoleType> getRoles() {
        throw new UnsupportedOperationException();
    }

    default Set<PermissionType> getPermissions() {
        throw new UnsupportedOperationException();
    }

    long getOrganizationId();

    default Set<String> getOauthScopes() {
        throw new UnsupportedOperationException();
    }
}
