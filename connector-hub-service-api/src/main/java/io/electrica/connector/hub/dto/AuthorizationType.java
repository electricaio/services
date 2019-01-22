package io.electrica.connector.hub.dto;

public enum AuthorizationType {

    None, Basic, AwsIam, Token, Ibm;

    public static final String BASIC = "Basic";
    public static final String AWS_IAM = "AwsIam";
    public static final String TOKEN = "Token";
    public static final String IBM = "Ibm";
}
