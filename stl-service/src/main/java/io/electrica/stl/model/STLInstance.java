package io.electrica.stl.model;

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
@Table(name = "stl_instances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"stl_id", "access_key_id"})
)
public class STLInstance extends AbstractEntity {

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "stl_id", nullable = false)
    private STL stl;

    @NotNull
    @Column(name = "access_key_id", nullable = false)
    private Long accessKeyId;

    public STLInstance(@NotNull STL stl, @NotNull Long accessKeyId) {
        this.stl = stl;
        this.accessKeyId = accessKeyId;
    }
}
