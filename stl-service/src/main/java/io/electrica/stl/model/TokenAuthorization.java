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
@Table(name = "token_authorizations")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TokenAuthorization extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 256)
    @Column(name = "token_hash")
    private String tokenHash;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id")
    private Authorization authorization;
}
