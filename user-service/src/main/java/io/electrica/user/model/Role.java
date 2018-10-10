package io.electrica.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

/**
 *  A Role.
 *
 */
@Getter
@Setter
@Entity
@Audited
@Table(name = "roles")
public class Role extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;
    @Column(length = 1023)
    private String description;

}
