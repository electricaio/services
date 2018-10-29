package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
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
@Table(name = "connections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"connector_id", "access_key_id"}),
        indexes = {
            @Index(name = "idx_connections_user_id", columnList = "user_id")
        }
)
public class Connection extends AbstractEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "connector_id", nullable = false)
    private Connector connector;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @NotNull
    @Column(name = "access_key_id", nullable = false)
    private Long accessKeyId;

    public Connection(Connector connector,
                      Long userId,
                      Long organizationId,
                      Long accessKeyId) {
        this.connector = connector;
        this.userId = userId;
        this.organizationId = organizationId;
        this.accessKeyId = accessKeyId;
    }
}
