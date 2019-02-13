package io.electrica.webhook.rest;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Getter
public class TypedDeferredResult<T> extends DeferredResult<ResponseEntity<T>> {
    private final String contentType;
    private final Long organizationId;
    private final Long userId;
    private final Long accessKeyId;


    public TypedDeferredResult(String contentType, Long timeout, Long organizationId, Long userId, Long accessKeyId) {
        super(timeout);
        this.contentType = contentType;
        this.organizationId = organizationId;
        this.userId = userId;
        this.accessKeyId = accessKeyId;
    }

    public boolean buildResponseEntityResult(T result) {
        return setResult(ResponseEntity.ok().header(CONTENT_TYPE, contentType).body(result));
    }
}
