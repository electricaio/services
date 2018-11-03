package io.electrica.connector.rest;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.PRIVATE;
import static io.electrica.common.rest.PathConstants.V1;

public interface ConnectorExecutorController {

    @PostMapping(PRIVATE + V1 + "/executors/sync")
    ResponseEntity<ConnectorExecutorResult> executeSync(@RequestBody ConnectorExecutorContext context);

    @PostMapping(PRIVATE + V1 + "/executors/async")
    ResponseEntity executeAsync(@RequestBody ConnectorExecutorContext context);

}
