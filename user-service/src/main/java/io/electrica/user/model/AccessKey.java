package io.electrica.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.electrica.common.jpa.model.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usr_access_keys")
public class AccessKey extends AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "key_name", length = 255, nullable = false)
    private String keyName;
    @NotNull
    @Column(name = "access_key", length = 255, nullable = false)
    private String accessKey;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_user_uid", nullable = false,
            foreignKey = @ForeignKey(name = "usr_access_keys_usr_user_uid_fkey"))
    private User user;

}
