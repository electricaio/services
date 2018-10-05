package io.electrica.stl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "basic_authorizations")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BasicAuthorization extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 256)
    @Column(name = "user_hash")
    private String userHash;

    @NotNull
    @Size(max = 256)
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id")
    private Authorization authorization;
}
