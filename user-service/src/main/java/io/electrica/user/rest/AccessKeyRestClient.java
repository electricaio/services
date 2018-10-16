package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.V1;

import java.util.List;

/**
 *  REST Client for Managing users.
 *
 */
public interface AccessKeyRestClient {

    //TODO review dto usage as a parameter during implementing authn for rest cals
    @PostMapping(V1 + "/access-keys")
    ResponseEntity<AccessKeyDto> generateAccessKey(@RequestBody AccessKeyDto accessKey);

    @GetMapping(V1 + "/users/{userId}/access-keys")
    ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@PathVariable Long userId);

    @GetMapping(V1 + "/access-keys/{accessKeyId}")
    ResponseEntity<AccessKeyDto> getAccessKey(@RequestBody AccessKeyDto accessKey);
}
