package io.electrica.invoker.rest;

import io.electrica.invoker.dto.InvocationContext;
import io.electrica.invoker.service.InvokerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

@RestController
public class InvokerControllerImpl implements InvokerController {

    private final InvokerService invokerService;

    @Inject
    public InvokerControllerImpl(InvokerService invokerService) {
        this.invokerService = invokerService;
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk') and #invoker.validateAccessKey()")
    public ResponseEntity<Object> invokeSync(@Valid @RequestBody InvocationContext context) {
        Object result = invokerService.invokeSync(context);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#invoker.validateAccessKey()")
    public ResponseEntity<Object> getConnections(String connectionName) {
        return ResponseEntity.ok(invokerService.getConnection(connectionName));
    }
}
