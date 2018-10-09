package io.electrica.stl.util;

import io.electrica.stl.model.AuthorizationType;

public class AuthorizationUtils {

    public static final String BASIC_AUTH = "basic_authorization";

    public static final String AWS_IAM_AUTH = "aws_iam_authorization";

    public static final String TOKEN_AUTH = "token_authorization";

    private AuthorizationUtils() {
    }

    public static boolean isBasicAuthorization(AuthorizationType type) {
        return BASIC_AUTH.equals(type.getName());
    }

    public static boolean isAwsIamAuthorization(AuthorizationType type) {
        return AWS_IAM_AUTH.equals(type.getName());
    }

    public static boolean isTokenAuthorization(AuthorizationType type) {
        return TOKEN_AUTH.equals(type.getName());
    }
}
