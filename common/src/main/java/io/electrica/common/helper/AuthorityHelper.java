package io.electrica.common.helper;

import io.electrica.common.exception.ActionForbiddenServiceException;
import io.electrica.common.security.PermissionType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorityHelper {

    private static final String DEFINED_PERMISSIONS_PREFIX = "dp:";
    private static final String ORGANIZATION_PREFIX = "org:";
    private static final String SEPARATOR = ",";

    private AuthorityHelper() {
    }

    public static String writeOrganization(long organizationId) {
        return ORGANIZATION_PREFIX + organizationId;
    }

    public static long readOrganization(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .filter(ga -> ga.getAuthority().startsWith(ORGANIZATION_PREFIX))
                .map(ga -> ga.getAuthority().substring(ORGANIZATION_PREFIX.length()))
                .findFirst()
                .map(Long::valueOf)
                .orElseThrow(() -> new ActionForbiddenServiceException("Organization authority required in token"));
    }

    public static String writePermissions(Set<PermissionType> permissions) {
        return DEFINED_PERMISSIONS_PREFIX + permissions.stream()
                .map(PermissionType::getStringCode)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Set<PermissionType> readPermissions(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .filter(ga -> ga.getAuthority().startsWith(DEFINED_PERMISSIONS_PREFIX))
                .map(ga -> ga.getAuthority().substring(DEFINED_PERMISSIONS_PREFIX.length()))
                .findFirst()
                .map(permissionsString -> Arrays.stream(permissionsString.split(SEPARATOR))
                        .map(PermissionType::parse)
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet());
    }

}
