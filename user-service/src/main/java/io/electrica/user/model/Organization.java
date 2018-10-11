package io.electrica.user.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    /*@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "organization")
    private List<User> users = new ArrayList<>();*/

}
