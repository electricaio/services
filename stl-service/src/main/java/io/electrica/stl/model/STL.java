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

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(name = "stls")
public class STL extends AbstractEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private STLType type;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

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
    @Column(nullable = false)
    private String ern;

}
