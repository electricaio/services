package io.electrica.stl.rest.impl;

import io.electrica.stl.rest.STLController;
import io.electrica.stl.rest.dto.STLDto;
import io.electrica.stl.service.STLService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class STLControllerImpl implements STLController {

    private STLService stlService;

    public STLControllerImpl(STLService stlService) {
        this.stlService = stlService;
    }

    @Override
    public ResponseEntity<List<STLDto>> list() {
        final List<STLDto> result = stlService.findAll();
        return ResponseEntity.ok().body(result);
    }
}
