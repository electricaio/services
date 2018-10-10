package io.electrica.stl.rest.impl;

import io.electrica.stl.rest.STLController;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.STLService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class STLControllerImpl implements STLController {

    private final STLService stlService;

    public STLControllerImpl(STLService stlService) {
        this.stlService = stlService;
    }

    @Override
    public ResponseEntity<List<ReadSTLDto>> list() {
        final List<ReadSTLDto> result = stlService.list();
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<ReadSTLDto> create(CreateSTLDto dto) {
        final ReadSTLDto result = stlService.create(dto);
        return ResponseEntity.ok().body(result);
    }
}
