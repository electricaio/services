package io.electrica.invoker.rest;

import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.dto.InvocationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.V1;

/**
 * Controller to invoke api.
 */
public interface InvokerController {

    @PostMapping(V1 + "/sdk/invoke-sync")
    ResponseEntity<ConnectorExecutorResult> invokeSync(@RequestBody InvocationContext context);

}
