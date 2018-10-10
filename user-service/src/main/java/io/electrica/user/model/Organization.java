package io.electrica.user.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Audited
@Table(name = "organizations")
public class Organization extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    private UUID uuid;
    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

}
