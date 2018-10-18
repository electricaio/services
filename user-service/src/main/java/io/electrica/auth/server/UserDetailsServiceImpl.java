package io.electrica.auth.server;

import io.electrica.common.helper.TokenHelper;
import io.electrica.common.security.PermissionType;
import io.electrica.common.security.RoleType;
import io.electrica.user.model.Role;
import io.electrica.user.model.RoleToPermission;
import io.electrica.user.model.User;
import io.electrica.user.model.UserToRole;
import io.electrica.user.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.electrica.common.helper.AuthorityHelper.*;

/**
 * UserDetailsServiceImpl provides authentication lookup service which validates the http header token.
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Provider<UserService> userService;

    @Inject
    public UserDetailsServiceImpl(Provider<UserService> userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUser(username);
        return new SpecialUserDetails(
                TokenHelper.buildIdTokenUsername(user.getId()),
                user.getSaltedPassword(),
                !user.getArchived(),
                true,
                true,
                true,
                buildGrantedAuthorities(user),
                null
        );
    }

    private User findUser(String username) throws UsernameNotFoundException {
        Optional<User> result = Optional.empty();
        if (TokenHelper.isIdTokenUsername(username)) {
            long id = TokenHelper.extractIdFromTokenUsername(username);
            result = userService.get().findByIdFetchingAuthorities(id);
        } else if (TokenHelper.isEmailTokenUsername(username)) {
            String email = TokenHelper.extractEmailFromTokenUsername(username);
            result = userService.get().findByEmailFetchingAuthorities(email);
        }
        return result.orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private Collection<GrantedAuthority> buildGrantedAuthorities(User user) {
        Long organizationId = user.getOrganization().getId();

        Set<RoleType> roles = new HashSet<>();
        Set<PermissionType> permissions = new HashSet<>();
        for (UserToRole userToRole : user.getUserToRoles()) {
            Role role = userToRole.getRole();
            roles.add(role.getType());
            for (RoleToPermission roleToPermission : role.getRoleToPermissions()) {
                permissions.add(roleToPermission.getPermission().getType());
            }
        }

        return Stream.of(writeOrganization(organizationId), writeRoles(roles), writePermissions(permissions))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
