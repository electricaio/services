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
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(name = "connectors",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "resource", "version"})
)
public class Connector extends AbstractEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ConnectorType type;

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_type_id", nullable = false)
    private AuthorizationType authorizationType;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 255)
    @Column(nullable = true)
    private String resource;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String version;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String namespace;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String ern;

    public Optional<String> getResourceOpt() {
        return Optional.ofNullable(resource);
    }
}
