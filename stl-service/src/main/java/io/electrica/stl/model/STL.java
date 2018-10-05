package io.electrica.stl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "stls")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class STL extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private STLType type;

    @NotNull
    @Size(max = 256)
    @Column(name = "name")
    private String name;

    @NotNull
    @Size(max = 256)
    @Column(name = "version")
    private String version;

    @NotNull
    @Size(max = 256)
    @Column(name = "namespace")
    private String namespace;

    @NotNull
    @Size(max = 256)
    @Column(name = "ern")
    private String ern;
}
