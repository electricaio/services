package io.electrica.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.electrica.common.security.RoleType;
import org.hibernate.envers.Audited;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A Role.
 */
@Getter
@Setter
@Entity
@Audited
@Table(name = "roles")
public class Role extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true, length = 63)
    @Enumerated(EnumType.STRING)
    private RoleType type;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Column(length = 1023)
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<UserToRole> userToRoles = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<RoleToPermission> roleToPermissions = new HashSet<>();

}
