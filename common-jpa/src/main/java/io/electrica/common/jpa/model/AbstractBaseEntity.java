package io.electrica.common.jpa.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Audited
@MappedSuperclass
public abstract class AbstractBaseEntity extends AbstractPersistable<Long> {

    @NotNull
    @Version
    @Column(name = "record_version", nullable = false)
    private Integer recordVersion;

}
