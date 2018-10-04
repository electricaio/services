package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "basic_authorizations")
public class BasicAuthorization extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 64)
    @Column(name = "user_hash")
    private String userHash;

    @NotNull
    @Size(max = 64)
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id")
    private Authorization authorization;

    public BasicAuthorization() {
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BasicAuthorization other = (BasicAuthorization) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (this.userHash != null && other.userHash != null && !this.userHash.equals(other.userHash))
            return false;
        if (this.passwordHash != null && other.passwordHash != null && !this.passwordHash.equals(other.passwordHash))
            return false;
        if (!((this.authorization == null && other.authorization == null)
                || (this.authorization != null && other.authorization != null && this.authorization.getId() == null && other.authorization.getId() == null)
                || this.authorization.getId().equals(other.authorization.getId())))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.userHash == null) ? 0 : this.userHash.hashCode());
        result = prime * result + ((this.passwordHash == null) ? 0 : this.passwordHash.hashCode());
        result = prime * result + ((this.authorization == null || this.authorization.getId() == null) ? 0 : this.authorization.getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "BasicAuthorization[" + "this.id=" + this.getId() + ", this.authorization=" + (this.authorization == null ? this.authorization : this.authorization.getId()) + "]";
    }

}
