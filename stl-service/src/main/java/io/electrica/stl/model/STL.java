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
@Table(name = "stls",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "resource", "version"})
)
public class STL extends AbstractEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private STLType type;

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

    public STL(STLType type, String name, String version, String namespace) {
        this.type = type;
        this.name = name;
        this.version = version;
        this.namespace = namespace;
    }

    public Optional<String> getResourceOpt() {
        return Optional.ofNullable(resource);
    }
}
