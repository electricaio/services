package io.electrica.webhook.dto;

public interface WebhookDto {

    String getSubmitUrl();

    void setSubmitUrl(String url);

    String getInvokeUrl();

    void setInvokeUrl(String url);

    String getPublicSubmitUrl();

    void setPublicSubmitUrl(String url);

    String getPublicInvokeUrl();

    void setPublicInvokeUrl(String url);

}
