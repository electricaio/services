package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;

/**
 * Access key controller for managing access keys.
 */
public interface AccessKeyController {

    @PostMapping(V1 + "/access-keys")
    ResponseEntity<AccessKeyDto> createAccessKey(@RequestBody CreateAccessKeyDto accessKey);

    @GetMapping(V1 + "/users/{userId}/access-keys")
    ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@PathVariable Long userId);

    @GetMapping(V1 + "/access-keys/{accessKeyId}")
    ResponseEntity<FullAccessKeyDto> getAccessKey(@PathVariable Long accessKeyId);

    @GetMapping(V1 + "/access-keys/{accessKeyId}/refresh")
    ResponseEntity<FullAccessKeyDto> refreshAccessKey(@PathVariable Long accessKeyId);

}
