package io.electrica.invoker.rest;

import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.dto.InvocationContext;
import io.electrica.invoker.dto.TinyConnectionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;

/**
 * Controller to invoke api.
 */
public interface InvokerController {

    @PostMapping(V1 + "/sdk/invoke-sync")
    ResponseEntity<ConnectorExecutorResult> invokeSync(@RequestBody InvocationContext context);

    @GetMapping(V1 + "/sdk/connections")
    ResponseEntity<List<TinyConnectionDto>> getConnections(
            @RequestParam(value = "connectionName", required = false) String connectionName,
            @RequestParam(value = "connector", required = true) String connector);
}
