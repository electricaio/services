package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
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

    @Column(nullable = true, unique = true)
    private UUID jti;

    @Size(max = 1023)
    @Column(nullable = true, length = 1023)
    private String key;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
