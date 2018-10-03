package io.electrica.services.stl.web.rest;

import io.electrica.services.stl.model.STL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("v1/api/stl") // todo change with constants from common's module
public interface STLController {

    @GetMapping(value = "/list")
    ResponseEntity<List<STL>> list();
}
