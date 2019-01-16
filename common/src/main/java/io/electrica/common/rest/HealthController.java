package io.electrica.common.rest;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final ApplicationContext applicationContext;

    public HealthController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping(PathConstants.HEALTH_PATH)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Ok");
    }
}
