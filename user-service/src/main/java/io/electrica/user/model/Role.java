package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import io.electrica.common.security.RoleType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

}
