package io.electrica.connector.rest;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectorExecutorControllerImpl implements ConnectorExecutorController {

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity<ConnectorExecutorResult> executeSync(@RequestBody ConnectorExecutorContext context) {
        // ToDo run executor
        ConnectorExecutorResult result = new ConnectorExecutorResult();
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity executeAsync(@RequestBody ConnectorExecutorContext context) {
        // ToDo run executor
        return ResponseEntity.accepted().build();
    }
}
