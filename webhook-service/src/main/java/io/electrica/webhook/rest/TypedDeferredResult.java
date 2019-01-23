package io.electrica.webhook.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class TypedDeferredResult<T> extends DeferredResult<ResponseEntity<T>> {
    private final String contentType;

    public TypedDeferredResult(String contentType, Long timeout) {
        super(timeout);
        this.contentType = contentType;
    }

    public boolean buildResponseEntityResult(T result) {
        return setResult(ResponseEntity.ok().header(CONTENT_TYPE, contentType).body(result));
    }
}
