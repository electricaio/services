package io.electrica.stl.rest;

import io.electrica.stl.rest.dto.STLDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;

@RequestMapping(V1 + "/stl")
public interface STLController {

    @GetMapping("/list")
    ResponseEntity<List<STLDto>> list();
}
