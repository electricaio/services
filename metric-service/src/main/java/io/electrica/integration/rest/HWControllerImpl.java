package io.electrica.integration.rest;

import io.electrica.metric.rest.HWController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HWControllerImpl implements HWController {

    @Override
    @PreAuthorize(" #common.hasPermission('ReadUser') ")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello world!");
    }
}
