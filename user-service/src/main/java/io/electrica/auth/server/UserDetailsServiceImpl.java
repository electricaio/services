package io.electrica.auth.server;

import io.electrica.common.helper.TokenHelper;
import io.electrica.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserDetailsServiceImpl provides authentication lookup service which validates the http header token.
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUser(username);
        return new SpecialUserDetails(
                TokenHelper.tokenUsernameFromId(user.getId()),
                user.getPassword(),
                // to DO !user.getArchived(),
                false,
                true,
                true,
                true,
                buildGrantedAuthorities(user),
                10000000L //user.getTokensNotBefore()
        );
    }

    private User findUser(String username) throws UsernameNotFoundException {
        Optional<User> result = Optional.empty();

/*        if (TokenHelper.isId(username)) {
            int id = TokenHelper.extractId(username);
            result = userService.get().findByIdHideArchived(id);
        } else if (TokenHelper.isEmail(username)) {
            String email = TokenHelper.extractEmail(username);
            result = userService.get().findByEmailHideArchived(email);
        } else if (TokenHelper.isPhone(username)) {
            String phone = TokenHelper.extractPhone(username);
            result = userService.get().findByPhoneHideArchived(phone);
        }*/

        return result.orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private Collection<GrantedAuthority> buildGrantedAuthorities(User user) {
        return Arrays.asList("AllUserManagementPermissions").stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        /*return userDtoService.get().buildAuthorities(user).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());*/
    }
}
