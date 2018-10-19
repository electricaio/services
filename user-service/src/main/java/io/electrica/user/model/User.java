package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * A User.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name = "users")
public class User extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String lastName;

    @NotNull
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(max = 127)
    @Column(nullable = false, length = 127)
    private String saltedPassword;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "user")
    private Set<UserToRole> userToRoles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<AccessKey> accessKeys = new HashSet<>();

}

