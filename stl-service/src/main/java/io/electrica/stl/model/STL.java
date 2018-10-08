package io.electrica.stl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "stls")
public class STL extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private STLType type;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "version", nullable = false, length = 255)
    private String version;

    @NotNull
    @Size(max = 255)
    @Column(name = "namespace", nullable = false, length = 255)
    private String namespace;

    @NotNull
    @Size(max = 255)
    @Column(name = "ern", nullable = false, length = 255)
    private String ern;
}
