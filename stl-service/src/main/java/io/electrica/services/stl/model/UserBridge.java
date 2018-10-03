package io.electrica.services.stl.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "users_bridge")
public class UserBridge extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserBridge() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UserBridge other = (UserBridge) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "UserBridge[" + "this.id=" + this.getId() + "]";
    }

}
