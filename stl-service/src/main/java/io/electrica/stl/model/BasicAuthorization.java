package io.electrica.stl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "basic_authorizations")
@Entity(name = "basic_authorizations")
public class BasicAuthorization extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 255)
    @Column(name = "user_hash")
    private String userHash;

    @NotNull
    @Size(max = 255)
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id")
    private Authorization authorization;
}

