package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "authorizations")
public class Authorization extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private AuthorizationType type;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "stl_instance_id")
    private STLInstance stlInstance;

    public Authorization() {
    }

    public AuthorizationType getType() {
        return type;
    }

    public void setType(AuthorizationType type) {
        this.type = type;
    }

    public STLInstance getStlInstance() {
        return stlInstance;
    }

    public void setStlInstance(STLInstance stlInstance) {
        this.stlInstance = stlInstance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Authorization other = (Authorization) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (!((this.type == null && other.type == null) || (this.type != null && other.type != null && this.type.getId() == null && other.type.getId() == null)
                || this.type.getId().equals(other.type.getId())))
            return false;

        if (!((this.stlInstance == null && other.stlInstance == null)
                || (this.stlInstance != null && other.stlInstance != null && this.stlInstance.getId() == null && other.stlInstance.getId() == null)
                || this.stlInstance.getId().equals(other.stlInstance.getId())))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.type == null || this.type.getId() == null) ? 0 : this.type.getId().hashCode());
        result = prime * result + ((this.stlInstance == null || this.stlInstance.getId() == null) ? 0 : this.stlInstance.getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Authorization[" + "this.id=" + this.getId() + ", this.type=" + (this.type == null ? this.type : this.type.getId()) + ", this.stlInstance="
                + (this.stlInstance == null ? this.stlInstance : this.stlInstance.getId()) + "]";
    }

}
