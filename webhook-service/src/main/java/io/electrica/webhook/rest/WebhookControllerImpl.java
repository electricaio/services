package io.electrica.webhook.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookControllerImpl implements io.electrica.webhook.rest.WebhookController {

    @Override
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }
}
