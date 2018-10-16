package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.service.AccessKeyDtoService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Client implementation for Managing users..
 */
@RestController
public class AccessKeyRestClientImpl implements AccessKeyRestClient {

    private final Logger logger = LoggerFactory.getLogger(AccessKeyRestClientImpl.class);

    private final AccessKeyDtoService accessKeyDtoService;

    public AccessKeyRestClientImpl(AccessKeyDtoService accessKeyDtoService) {
        this.accessKeyDtoService = accessKeyDtoService;
    }

    @Override
    public ResponseEntity<AccessKeyDto> generateAccessKey(@RequestBody AccessKeyDto accessKey) {
        logger.info("REST request to generate access key : {}", accessKey);
        return ResponseEntity.ok(accessKeyDtoService.create(accessKey));
    }

    @Override
    public ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@RequestBody AccessKeyDto accessKey) {
        Long userId = accessKey.getUserId();
        logger.info("REST request to get access keys for user: {}", userId);
        return ResponseEntity.ok(accessKeyDtoService.findAllNonArchivedByUser(userId));
    }

    @Override
    public ResponseEntity<AccessKeyDto> getAccessKey(@RequestBody AccessKeyDto accessKey) {
        Long accessKeyId = accessKey.getId();
        Long userId = accessKey.getUserId();
        logger.info("REST request to get access key : {} for user: {}", accessKeyId, userId);
        return ResponseEntity.ok(accessKeyDtoService.findByKeyAndUser(accessKeyId, userId));
    }

}
