package io.electrica.invoker.rest;

import io.electrica.invoker.dto.InvokationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.V1;

/**
 * Controller to invoke api.
 */
public interface InvokerController {

    @PostMapping(V1 + "/invokers/sync")
    ResponseEntity<Boolean> invokeSync(@RequestBody InvokationContext context);

}
