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
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false, unique = true)
    private UUID jti;

    @NotNull
    @Size(max = 1023)
    @Column(nullable = false, length = 1023)
    private String key;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
