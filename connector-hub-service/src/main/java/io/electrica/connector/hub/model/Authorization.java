package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter

@Entity
@Audited
@Table(name = "authorizations")
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Authorization extends AbstractEntity {

    @OneToOne(mappedBy = "authorization")
    private Connection connection;

}
