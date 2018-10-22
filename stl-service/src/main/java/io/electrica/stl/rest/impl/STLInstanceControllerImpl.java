package io.electrica.stl.rest.impl;

import io.electrica.stl.rest.STLInstanceController;
import io.electrica.stl.rest.dto.CreateSTLInstanceDto;
import io.electrica.stl.rest.dto.ReadSTLInstanceDto;
import io.electrica.stl.service.STLInstanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class STLInstanceControllerImpl implements STLInstanceController {

    private final STLInstanceService stlInstanceService;

    public STLInstanceControllerImpl(STLInstanceService stlInstanceService) {
        this.stlInstanceService = stlInstanceService;
    }


    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToSTL')")
    public ResponseEntity<ReadSTLInstanceDto> create(@Valid @RequestBody CreateSTLInstanceDto request) {
        final ReadSTLInstanceDto dto = stlInstanceService.create(request);
        return ResponseEntity.ok().body(dto);
    }
}
