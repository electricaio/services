package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "access_keys_bridge")
public class AccessKeyBridge extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserBridge user;

    public AccessKeyBridge() {
    }

    public UserBridge getUser() {
        return user;
    }

    public void setUser(UserBridge user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AccessKeyBridge other = (AccessKeyBridge) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (!((this.user == null && other.user == null) || (this.user != null && other.user != null && this.user.getId() == null && other.user.getId() == null)
                || this.user.getId().equals(other.user.getId())))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.user == null || this.user.getId() == null) ? 0 : this.user.getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AccessKeyBridge[" + "this.id=" + this.getId() + ", this.user=" + (this.user == null ? this.user : this.user.getId()) + "]";
    }

}
