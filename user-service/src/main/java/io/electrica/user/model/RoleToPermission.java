package io.electrica.user.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.electrica.common.jpa.model.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usr_roles_to_usr_permissions_map", indexes = { 
        @Index (columnList = "usr_role_id", name = "usr_roles_to_usr_permissions_map_usr_role_id_idx"),
        @Index (columnList = "usr_permission_id", name = "usr_roles_to_usr_permissions_map_usr_permission_id_idx")
})
public class RoleToPermission extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_role_id", nullable = false,
            foreignKey = @ForeignKey(name = "usr_roles_to_usr_permissions_map_usr_role_id_fkey"))
    private Role role;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_permission_id", nullable = false,
            foreignKey = @ForeignKey(name = "usr_roles_to_usr_permissions_map_usr_permission_id_fkey"))
    private Permission permission;

}
