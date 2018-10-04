package io.electrica.stl.rest;

import io.electrica.stl.dto.TestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StlRestClientImpl implements StlRestClient {

    @Override
    public ResponseEntity<TestDto> createTest(@RequestBody TestDto user) {
        // ToDo stubbed
        return ResponseEntity.ok(new TestDto());
    }

}
