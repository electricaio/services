package io.electrica.user.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.electrica.common.jpa.model.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usr_organizations")
public class Organization extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "uuid")
    private UUID uuid;
    @NotNull
    @Column(length = 255, nullable = false)
    private String name;

}
