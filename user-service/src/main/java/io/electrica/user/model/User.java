package io.electrica.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;


/**
 *  A model object to represent user attributes.
 */
@Getter
@Setter
public class User  {

    @Column(nullable =  false)
    private Integer id;

    @Column(nullable = false, length = 31)
    private String firstName;

    @Column(length = 31)
    private String middleName;

    @Column(nullable = false, length = 31)
    private String lastName;

    @Column(nullable = false, length = 63, unique = true)
    private String email;

    @Column(length = 15, unique = true)
    private String phone;

    @Column(nullable = false, length = 127)
    private String password;
}

