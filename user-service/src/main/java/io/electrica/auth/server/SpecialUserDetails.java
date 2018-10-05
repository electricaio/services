package io.electrica.auth.server;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

/**
 * A  Model object to support for token revoke or expire for a user.
 *
 */
@Getter
public class SpecialUserDetails extends User {

    private final Long tokensNotBefore;

    public SpecialUserDetails(
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities,
            Long tokensNotBefore
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.tokensNotBefore = tokensNotBefore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpecialUserDetails that = (SpecialUserDetails) o;
        return Objects.equals(tokensNotBefore, that.tokensNotBefore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tokensNotBefore);
    }
}
