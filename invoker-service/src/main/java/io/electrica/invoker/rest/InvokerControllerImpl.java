package io.electrica.invoker.rest;

import io.electrica.invoker.dto.InvokationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvokerControllerImpl implements InvokerController {

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk') and #invoker.validateAccessKey()")
    public ResponseEntity<Boolean> invokeSync(@RequestBody InvokationContext context) {
        return ResponseEntity.ok(true);
    }

}
