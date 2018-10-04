package io.electrica.stl.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "stl_types")
public class STLType extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    public STLType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final STLType other = (STLType) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (this.name != null && other.name != null && !this.name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "STLType[" + "this.id=" + this.getId() + ", this.name=" + this.name + "]";
    }

}
