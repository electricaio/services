package io.electrica.stl.rest.impl;

import io.electrica.stl.rest.STLInstanceController;
import io.electrica.stl.rest.dto.CreateSTLInstance;
import io.electrica.stl.service.STLInstanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class STLInstanceControllerImpl implements STLInstanceController {

    private STLInstanceService stlInstanceService;

    private STLInstanceControllerImpl(STLInstanceService stlInstanceService) {
        this.stlInstanceService = stlInstanceService;
    }

    @Override
    public ResponseEntity<Void> create(@Valid @RequestBody CreateSTLInstance request) {
        // todo check permission STLCreate for Org-user
        // todo no permission for Org-Admin and Super-Admin

        stlInstanceService.create(request.getStlId(), request.getAccessKeyId(), request.getAuthorizationData());

        return ResponseEntity.ok().build();
    }
}
