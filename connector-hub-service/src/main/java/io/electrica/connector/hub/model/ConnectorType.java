package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(name = "connector_types")
public class ConnectorType extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    public ConnectorType(String name) {
        this.name = name;
    }
}
