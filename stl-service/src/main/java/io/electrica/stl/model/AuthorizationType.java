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
@Entity(name = "authorization_types")
public class AuthorizationType extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;
}

