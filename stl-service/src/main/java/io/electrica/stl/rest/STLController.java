package io.electrica.stl.rest;

import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

public interface STLController {

    @GetMapping(PUBLIC + V1 + "/stls")
    ResponseEntity<List<ReadSTLDto>> findAll();

    @PostMapping(V1 + "/stls")
    ResponseEntity<ReadSTLDto> create(CreateSTLDto request);
}
