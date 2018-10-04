package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "stl_instances")
public class STLInstance extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "stl_id")
    private STL stl;

    @NotNull
    @Column(name = "access_key_id")
    private Long accessKeyId;

    public STLInstance() {
    }

    public STL getStl() {
        return stl;
    }

    public void setStl(STL stl) {
        this.stl = stl;
    }

    public Long getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(Long accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final STLInstance other = (STLInstance) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (!((this.stl == null && other.stl == null) || (this.stl != null && other.stl != null && this.stl.getId() == null && other.stl.getId() == null)
                || this.stl.getId().equals(other.stl.getId())))
            return false;
        if (this.accessKeyId != null && other.accessKeyId != null && !this.accessKeyId.equals(other.accessKeyId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.stl == null || this.stl.getId() == null) ? 0 : this.stl.getId().hashCode());
        result = prime * result + ((this.accessKeyId == null) ? 0 : this.accessKeyId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "STLInstance[" + "this.id=" + this.getId() + ", this.stl=" + (this.stl == null ? this.stl : this.stl.getId()) + ", this.accessKey="
                +  "this.accessKeyId=" + this.accessKeyId + "]";
    }

}
