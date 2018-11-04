package io.electrica.connector.hub.model;

import io.electrica.connector.hub.dto.AuthorizationType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter

@Entity
@Audited
@DiscriminatorValue(AuthorizationType.BASIC)
public class BasicAuthorization extends Authorization {

    @NotNull
    @Size(max = 255)
    @Column
    private String username;

    @NotNull
    @Size(max = 255)
    @Column
    private String password;

}
