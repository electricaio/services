package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.PRIVATE;
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

    @PutMapping(V1 + "/access-keys/{accessKeyId}/refresh")
    ResponseEntity<AccessKeyDto> refreshAccessKey(@PathVariable Long accessKeyId);

    @PostMapping(PRIVATE + V1 + "/access-keys/{accessKeyId}/validate")
    ResponseEntity<Boolean> validateAccessKey(@PathVariable Long accessKeyId);

    @PostMapping(PRIVATE + V1 + "/access-keys/jti/{jti}/validate")
    ResponseEntity<Boolean> validateJti(@PathVariable UUID jti);

}
