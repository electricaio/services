package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter

@Entity
@Audited
@Table(
        name = "connections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "access_key_id"}),
        indexes = @Index(name = "connections_user_id_idx", columnList = "user_id")
)
public class Connection extends AbstractEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    @OneToOne
    @JoinColumn(name = "authorization_id", unique = true)
    private Authorization authorization;

}
