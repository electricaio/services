package io.electrica.stl.model;

import io.electrica.common.jpa.model.AbstractEntity;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(name = "authorization_types")
public class AuthorizationType extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private AuthorizationTypeName name;

    public AuthorizationType(AuthorizationTypeName name) {
        this.name = name;
    }
}

