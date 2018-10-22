package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.rest.dto.CreateConnectorDto;
import io.electrica.connector.hub.rest.dto.ReadConnectorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

public interface ConnectorController {

    @GetMapping(PUBLIC + V1 + "/connectors")
    ResponseEntity<List<ReadConnectorDto>> findAll();

    @PostMapping(V1 + "/connectors")
    ResponseEntity<ReadConnectorDto> create(CreateConnectorDto request);
}
