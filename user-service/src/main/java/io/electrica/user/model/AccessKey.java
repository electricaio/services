package io.electrica.user.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

/**
 *  An Access Key.
 *
 */

@Getter
@Setter
@Entity
@Audited
@Table(
        name = "access_keys",
        uniqueConstraints = @UniqueConstraint(columnNames = {"keyName", "user_id"})
)
public class AccessKey extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String keyName;
    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String accessKey;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
