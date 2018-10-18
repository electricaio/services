package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import io.electrica.common.security.PermissionType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * A Permission.
 */
@Getter
@Setter
@Entity
@Audited
@Table(name = "permissions")
public class Permission extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true, length = 63)
    @Enumerated(EnumType.STRING)
    private PermissionType type;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Column(length = 1023)
    private String description;

    @OneToMany(mappedBy = "permission")
    private Set<RoleToPermission> roleToPermissions = new HashSet<>();

    @OneToMany(mappedBy = "permission")
    private Set<PermissionCategories> permissionCategories = new HashSet<>();

}
