package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "token_authorizations")
public class TokenAuthorization extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 64)
    @Column(name = "token_hash")
    private String tokenHash;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id")
    private Authorization authorization;

    public TokenAuthorization() {
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
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
        final TokenAuthorization other = (TokenAuthorization) obj;
        if (this.getId() != null && other.getId() != null && !this.getId().equals(other.getId()))
            return false;
        if (this.tokenHash != null && other.tokenHash != null && !this.tokenHash.equals(other.tokenHash))
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
        result = prime * result + ((this.tokenHash == null) ? 0 : this.tokenHash.hashCode());
        result = prime * result + ((this.authorization == null || this.authorization.getId() == null) ? 0 : this.authorization.getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "TokenAuthorization[" + "this.id=" + this.getId() + ", this.authorization=" + (this.authorization == null ? this.authorization : this.authorization.getId()) + "]";
    }

}
