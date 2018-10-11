package io.electrica.stl.rest.impl;

import io.electrica.stl.api.STLApi;
import io.electrica.stl.rest.STLController;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class STLControllerImpl implements STLController {

    private final STLApi stlApi;

    public STLControllerImpl(STLApi stlApi) {
        this.stlApi = stlApi;
    }

    @Override
    public ResponseEntity<List<ReadSTLDto>> findAll() {
        final List<ReadSTLDto> result = stlApi.findAll();
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<ReadSTLDto> create(CreateSTLDto dto) {
        final ReadSTLDto result = stlApi.create(dto);
        return ResponseEntity.ok().body(result);
    }
}
