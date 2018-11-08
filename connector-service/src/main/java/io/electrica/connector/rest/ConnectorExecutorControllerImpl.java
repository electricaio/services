package io.electrica.connector.rest;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ConnectorExecutorControllerImpl implements ConnectorExecutorController {

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity<ConnectorExecutorResult> executeSync(@Valid @RequestBody ConnectorExecutorContext context) {

        ConnectorExecutorResult result = new ConnectorExecutorResult();
        result.setConnectionId(context.getConnection().getConnection().getId());
        result.setInstanceId(context.getInvocationContext().getInstanceId());
        result.setInvocationId(context.getInvocationId());
        result.setSuccess(true);

        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity executeAsync(@Valid @RequestBody ConnectorExecutorContext context) {
        // ToDo run executor
        return ResponseEntity.accepted().build();
    }
}
