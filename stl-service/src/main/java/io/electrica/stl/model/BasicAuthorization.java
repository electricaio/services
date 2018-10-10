package io.electrica.stl.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(name = "basic_authorizations")
public class BasicAuthorization extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String userHash;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String passwordHash;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id", nullable = false)
    private Authorization authorization;

}

