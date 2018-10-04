package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "stls")
public class STL extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private STLType type;

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "version")
    private String version;

    @NotNull
    @Size(max = 255)
    @Column(name = "namespace")
    private String namespace;

    @NotNull
    @Size(max = 255)
    @Column(name = "ern")
    private String ern;

    public STL() {
    }

    public STLType getType() {
        return type;
    }

    public void setType(STLType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getErn() {
        return ern;
    }

    public void setErn(String ern) {
        this.ern = ern;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final STL other = (STL) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (!((this.type == null && other.type == null) || (this.type != null && other.type != null && this.type.getId() == null && other.type.getId() == null)
                || this.type.getId().equals(other.type.getId())))
            return false;

        if (this.name != null && other.name != null && !this.name.equals(other.name))
            return false;
        if (this.version != null && other.version != null && !this.version.equals(other.version))
            return false;
        if (this.namespace != null && other.namespace != null && !this.namespace.equals(other.namespace))
            return false;
        if (this.ern != null && other.ern != null && !this.ern.equals(other.ern))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.type == null || this.type.getId() == null) ? 0 : this.type.getId().hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.version == null) ? 0 : this.version.hashCode());
        result = prime * result + ((this.namespace == null) ? 0 : this.namespace.hashCode());
        result = prime * result + ((this.ern == null) ? 0 : this.ern.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "STL[" + "this.id=" + this.getId() + ", this.type=" + (this.type == null ? this.type : this.type.getId()) + ", this.name=" + this.name + ", this.version=" + this.version
                + ", this.namespace=" + this.namespace + ", this.ern=" + this.ern + "]";
    }

}
