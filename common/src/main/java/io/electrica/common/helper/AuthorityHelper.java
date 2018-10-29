package io.electrica.common.helper;

import io.electrica.common.exception.ActionForbiddenServiceException;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthorityHelper {

    private static final String DEFINED_ROLES_PREFIX = "dr:";
    private static final String DEFINED_PERMISSIONS_PREFIX = "dp:";
    private static final String ORGANIZATION_PREFIX = "org:";
    private static final String SEPARATOR = ";";

    private AuthorityHelper() {
    }

    public static Collection<? extends GrantedAuthority> buildGrantedAuthorities(
            Long organizationId,
            Set<RoleType> roles,
            Set<PermissionType> permissions
    ) {
        String rolesAuthority = AuthorityHelper.writeRoles(roles);
        String permissionsAuthority = AuthorityHelper.writePermissions(permissions);
        String organizationAuthority = AuthorityHelper.writeOrganization(organizationId);
        return Stream.of(rolesAuthority, permissionsAuthority, organizationAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

    }

    private static Optional<String> findByPrefix(Collection<? extends GrantedAuthority> authorities, String prefix) {
        return authorities.stream()
                .filter(ga -> ga.getAuthority().startsWith(prefix))
                .map(ga -> ga.getAuthority().substring(prefix.length()))
                .findFirst();
    }

    private static String writeOrganization(Long organizationId) {
        return ORGANIZATION_PREFIX + organizationId;
    }

    public static long readOrganization(Collection<? extends GrantedAuthority> authorities) {
        return findByPrefix(authorities, ORGANIZATION_PREFIX)
                .map(Long::valueOf)
                .orElseThrow(() -> new ActionForbiddenServiceException("Organization authority required in token"));
    }

    private static String writeRoles(Set<RoleType> roles) {
        return DEFINED_ROLES_PREFIX + roles.stream()
                .map(RoleType::getStringCode)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Set<RoleType> readRoles(Collection<? extends GrantedAuthority> authorities) {
        return findByPrefix(authorities, DEFINED_ROLES_PREFIX)
                .map(rolesString -> Arrays.stream(rolesString.split(SEPARATOR))
                        .map(RoleType::parse)
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet());
    }

    private static String writePermissions(Set<PermissionType> permissions) {
        return DEFINED_PERMISSIONS_PREFIX + permissions.stream()
                .map(PermissionType::getStringCode)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Set<PermissionType> readPermissions(Collection<? extends GrantedAuthority> authorities) {
        return findByPrefix(authorities, DEFINED_PERMISSIONS_PREFIX)
                .map(permissionsString -> Arrays.stream(permissionsString.split(SEPARATOR))
                        .map(PermissionType::parse)
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet());
    }

}
