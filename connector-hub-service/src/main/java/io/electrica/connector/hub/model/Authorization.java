package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter

@Entity
@Audited
@Table(name = "authorizations")
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Authorization extends AbstractEntity {

    @Size(max = 255)
    @Column(name = "tenant_ref_id")
    private String tenantRefId;

    @OneToOne(mappedBy = "authorization")
    private Connection connection;

}
