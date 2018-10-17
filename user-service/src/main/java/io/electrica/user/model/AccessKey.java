package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * An Access Key.
 */
@Getter
@Setter
@Entity
@Audited
@Table(
        name = "access_keys",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"})
)
public class AccessKey extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String key;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
