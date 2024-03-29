package io.electrica.common.jpa.model;

import io.electrica.common.jpa.hibernate.JsonObjectUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@Audited
@MappedSuperclass
@TypeDef(name = AbstractEntity.JSONB_TYPE, typeClass = JsonObjectUserType.class)
public abstract class AbstractEntity extends AbstractPersistable<Long> implements CommonEntity {

    protected static final String JSONB_TYPE = "jsonb";

    @Column(nullable = false)
    private Boolean archived = false;

    @Version
    @Column(nullable = false)
    private Long revisionVersion;

    @Override
    public Long getRevisionVersion() {
        return revisionVersion;
    }

    @Override
    public void setRevisionVersion(Long version) {
        this.revisionVersion = version;
    }

    @Override
    public Boolean getArchived() {
        return archived;
    }

    @Override
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // change modifier
    @Override
    public void setId(Long id) {
        super.setId(id);
    }

}
