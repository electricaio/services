package io.electrica.stl.rest;

import io.electrica.stl.rest.dto.CreateSTLInstanceDto;
import io.electrica.stl.rest.dto.ReadSTLInstanceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.electrica.common.rest.PathConstants.V1;

@RequestMapping(V1 + "/stl-instances")
public interface STLInstanceController {

    @PostMapping
    ResponseEntity<ReadSTLInstanceDto> create(CreateSTLInstanceDto request);
}
