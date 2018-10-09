package io.electrica.user.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "users")
public class User extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    private UUID uuid;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

}

