package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PRIVATE;
import static io.electrica.common.rest.PathConstants.V1;

/**
 * Access key controller for managing access keys.
 */
public interface AccessKeyController {

    @PostMapping(V1 + "/access-keys")
    ResponseEntity<AccessKeyDto> createAccessKey(@RequestBody CreateAccessKeyDto accessKey);

    @GetMapping(V1 + "/users/{userId}/access-keys")
    ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@PathVariable("userId") Long userId);

    @GetMapping(V1 + "/access-keys/{accessKeyId}")
    ResponseEntity<FullAccessKeyDto> getAccessKey(@PathVariable("accessKeyId") Long accessKeyId);

    @PutMapping(V1 + "/access-keys/{accessKeyId}/refresh")
    ResponseEntity<AccessKeyDto> refreshAccessKey(@PathVariable("accessKeyId") Long accessKeyId);

    /**
     * Check if specified {@code accessKeyId} belongs session user.
     */
    @PostMapping(PRIVATE + V1 + "/me/access-keys/{accessKeyId}/validate")
    ResponseEntity<Boolean> validateMyAccessKeyById(@PathVariable("accessKeyId") Long accessKeyId);

    /**
     * Check if session access key belongs session user and hasn't been revoked.
     * Access key identified by oauth2 token JTI.
     */
    @PostMapping(PRIVATE + V1 + "/me/access-keys/validate")
    ResponseEntity<Boolean> validateMyAccessKey();

    @DeleteMapping(V1 + "/access-keys/{accessKeyId}")
    ResponseEntity<AccessKeyDto> deleteAccessKey(@PathVariable("accessKeyId") Long accessKeyId);

}
