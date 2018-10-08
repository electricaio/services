package io.electrica.stl.model;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "authorizations")
public class Authorization extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private AuthorizationType type;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "stl_instance_id", nullable = false)
    private STLInstance stlInstance;
}
