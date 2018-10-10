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

    @GetMapping(PUBLIC + V1 + "/stl/list")
    ResponseEntity<List<ReadSTLDto>> list();

    @PostMapping(V1 + "/stl")
    ResponseEntity<ReadSTLDto> create(CreateSTLDto request);
}
