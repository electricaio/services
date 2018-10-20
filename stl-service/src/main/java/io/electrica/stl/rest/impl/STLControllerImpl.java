package io.electrica.stl.rest.impl;

import io.electrica.stl.rest.STLController;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.STLDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class STLControllerImpl implements STLController {

    private final STLDtoService stlDtoService;

    public STLControllerImpl(STLDtoService stlDtoService) {
        this.stlDtoService = stlDtoService;
    }

    @Override
    public ResponseEntity<List<ReadSTLDto>> findAll() {
        final List<ReadSTLDto> result = stlDtoService.findAll();
        return ResponseEntity.ok().body(result);
    }

    @Override
    @PreAuthorize(" #common.hasPermission('CreateSTL') AND #common.isSuperAdmin() ")
    public ResponseEntity<ReadSTLDto> create(@Valid @RequestBody CreateSTLDto dto) {
        final ReadSTLDto result = stlDtoService.create(dto);
        return ResponseEntity.ok().body(result);
    }
}
