package io.electrica.connector.rest;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.service.ExecutorManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

@RestController
public class ConnectorExecutorControllerImpl implements ConnectorExecutorController {

    private final ExecutorManager executorManager;

    @Inject
    public ConnectorExecutorControllerImpl(ExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity<ConnectorExecutorResult> executeSync(@Valid @RequestBody ConnectorExecutorContext context) {
        ConnectorExecutorResult result = executorManager.execute(context);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity executeAsync(@Valid @RequestBody ConnectorExecutorContext context) {
        // ToDo run executor
        return ResponseEntity.accepted().build();
    }
}
