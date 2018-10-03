package io.electrica.services.stl.web.rest.impl;

import io.electrica.services.stl.model.STL;
import io.electrica.services.stl.service.STLService;
import io.electrica.services.stl.web.rest.STLController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class STLControllerImpl implements STLController {

    private STLService stlService;

    public STLControllerImpl(STLService stlService) {
        this.stlService = stlService;
    }

    public ResponseEntity<List<STL>> list() {
        final List<STL> result = stlService.findAll();
        return ResponseEntity.ok().body(result);
    }
}
