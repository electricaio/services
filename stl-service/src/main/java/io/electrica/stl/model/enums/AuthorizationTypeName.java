package io.electrica.stl.model.enums;

public enum AuthorizationTypeName {

    BASIC_AUTHORIZATION("BASIC_AUTHORIZATION"),

    AWS_IAM_AUTHORIZATION("AWS_IAM_AUTHORIZATION"),

    TOKEN_AUTHORIZATION("TOKEN_AUTHORIZATION");

    private String name;

    AuthorizationTypeName(String name) {
        this.name = name;
    }
}
