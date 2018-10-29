package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(
        name = "authorizations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "connection_id"})
        }
)
public class Authorization extends AbstractEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "tenant_ref_id", nullable = true)
    private String tenantRefId;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private AuthorizationType type;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id", nullable = false)
    private Connection connection;
}
