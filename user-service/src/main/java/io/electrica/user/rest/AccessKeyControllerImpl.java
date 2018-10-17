package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.service.AccessKeyDtoService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Access key controller implementation.
 */
@RestController
public class AccessKeyControllerImpl implements AccessKeyController {

    private final Logger logger = LoggerFactory.getLogger(AccessKeyControllerImpl.class);

    private final AccessKeyDtoService accessKeyDtoService;

    public AccessKeyControllerImpl(AccessKeyDtoService accessKeyDtoService) {
        this.accessKeyDtoService = accessKeyDtoService;
    }

    @Override
    public ResponseEntity<AccessKeyDto> generateAccessKey(@RequestBody AccessKeyDto accessKey) {
        logger.info("REST request to generate access key : {}", accessKey);
        return ResponseEntity.ok(accessKeyDtoService.create(accessKey));
    }

    @Override
    public ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@PathVariable Long userId) {
        logger.info("REST request to get access keys for user: {}", userId);
        return ResponseEntity.ok(accessKeyDtoService.findAllNonArchivedByUser(userId));
    }

    @Override
    public ResponseEntity<AccessKeyDto> getAccessKey(@PathVariable Long accessKeyId, @PathVariable Long userId) {
        logger.info("REST request to get access key : {} for user: {}", accessKeyId, userId);
        return ResponseEntity.ok(accessKeyDtoService.findByKeyAndUser(accessKeyId, userId));
    }

}
