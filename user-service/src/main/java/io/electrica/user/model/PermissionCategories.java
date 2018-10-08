package io.electrica.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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
@Table(name = "usr_permission_categories")
public class PermissionCategories extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(length = 255, nullable = false)
    private String category;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_permission_id", nullable = false,
                foreignKey = @ForeignKey(name = "usr_permission_categories_usr_permission_id_fkey"))
    private Permission permission;
}
