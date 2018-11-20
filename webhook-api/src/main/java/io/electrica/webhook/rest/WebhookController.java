package io.electrica.webhook.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

public interface WebhookController {

    @GetMapping(PUBLIC  + V1 + "/webhhook")
    ResponseEntity<String> hello();

}
