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
@DiscriminatorValue(AuthorizationType.IBM)
public class IbmAuthorization extends Authorization {

    @NotNull
    @Size(max = 31)
    @Column(length = 31)
    private String integrationId;

    @NotNull
    @Size(max = 31)
    @Column(length = 31)
    private String clientId;
}
