package io.electrica.stl.rest;


import io.electrica.stl.dto.TestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

public interface StlRestClient {

    @PostMapping(PUBLIC + V1 + "/tests")
    ResponseEntity<TestDto> createTest(@RequestBody TestDto user);

}
