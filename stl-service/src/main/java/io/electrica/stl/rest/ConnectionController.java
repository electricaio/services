package io.electrica.stl.rest;

import io.electrica.stl.rest.dto.ConnectDto;
import io.electrica.stl.rest.dto.ConnectionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.electrica.common.rest.PathConstants.V1;

@RequestMapping(V1 + "/connections")
public interface ConnectionController {

    @PostMapping
    ResponseEntity<ConnectionDto> connect(ConnectDto request);
}
