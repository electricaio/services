package io.electrica.metric.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import static io.electrica.common.rest.PathConstants.V1;

public interface HWController {

    @GetMapping(V1 + "/integrations/hello")
    ResponseEntity<String> hello();
}
