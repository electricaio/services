package io.electrica.webhook.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

@RestController
public class WebsocketController {

    @GetMapping(PUBLIC + V1 + "/websockets/hello")
    ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }
}
