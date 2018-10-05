package io.electrica.user.model;

import lombok.Getter;
import lombok.Setter;


/**
 *  A model object to represent user attributes.
 */
@Getter
@Setter
public class User  {

    private Integer id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String phone;

    private String password;
}

