package io.electrica.stl.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Table(name = "aws_iam_authorizations")
public class AwsIamAuthorization extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 255)
    @Column(name = "details")
    private String details;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_id")
    private Authorization authorization;

    public AwsIamAuthorization() {
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
        final AwsIamAuthorization other = (AwsIamAuthorization) obj;
        if (this.details != null && other.details != null && !this.details.equals(other.details))
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
        result = prime * result + ((this.details == null) ? 0 : this.details.hashCode());
        result = prime * result + ((this.authorization == null || this.authorization.getId() == null) ? 0 : this.authorization.getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AwsIamAuthorization[" + "this.id=" + this.getId() + ", this.details=" + this.details + ", this.authorization="
                + (this.authorization == null ? this.authorization : this.authorization.getId()) + "]";
    }

}
