package io.electrica.stl.rest;

import io.electrica.stl.rest.dto.CreateSTLInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.electrica.common.rest.PathConstants.V1;

@RequestMapping(V1 + "/stl-instance")
public interface STLInstanceController {

    @PostMapping
    ResponseEntity<Void> create(CreateSTLInstance request);
}
