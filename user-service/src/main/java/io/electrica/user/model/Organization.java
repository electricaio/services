package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * A Organization.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name = "organizations")
public class Organization extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "organization")
    private Set<User> users = new HashSet<>();

}
