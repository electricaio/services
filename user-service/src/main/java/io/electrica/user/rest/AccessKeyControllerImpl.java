package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.service.dto.AccessKeyDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * Access key controller implementation.
 */
@RestController
public class AccessKeyControllerImpl implements AccessKeyController {

    private final Logger logger = LoggerFactory.getLogger(AccessKeyControllerImpl.class);

    private final AccessKeyDtoService accessKeyDtoService;

    @Inject
    public AccessKeyControllerImpl(AccessKeyDtoService accessKeyDtoService) {
        this.accessKeyDtoService = accessKeyDtoService;
    }

    @Override
    @PreAuthorize("#common.hasPermission('CreateAccessKey') AND  " +
            "( #common.isSuperAdmin() OR #common.isUser(#accessKey.getUserId()) )")
    public ResponseEntity<AccessKeyDto> createAccessKey(@Valid @RequestBody CreateAccessKeyDto accessKey) {
        AccessKeyDto result = accessKeyDtoService.create(accessKey);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadAccessKey') AND  ( #common.isSuperAdmin() OR #common.isUser(#userId) )")
    public ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@PathVariable("userId") Long userId) {
        List<AccessKeyDto> result = accessKeyDtoService.findByUser(userId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadAccessKey')")
    @PostAuthorize("#common.isUser(returnObject.getBody().getUserId()) OR #common.isSuperAdmin()")
    public ResponseEntity<FullAccessKeyDto> getAccessKey(@PathVariable("accessKeyId") Long accessKeyId) {
        return ResponseEntity.ok(accessKeyDtoService.findByKey(accessKeyId));
    }

    @Override
    @PreAuthorize("#common.hasPermission('CreateAccessKey') and  #user.isUserAccessKey(#accessKeyId)")
    public ResponseEntity<AccessKeyDto> refreshAccessKey(@PathVariable("accessKeyId") Long accessKeyId) {
        return ResponseEntity.ok(accessKeyDtoService.refreshKey(accessKeyId));
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadAccessKey')")
    public ResponseEntity<Boolean> validateMyAccessKeyById(@PathVariable("accessKeyId") Long accessKeyId) {
        Boolean result = accessKeyDtoService.validateMyAccessKeyById(accessKeyId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity<Boolean> validateMyAccessKey() {
        Boolean result = accessKeyDtoService.validateMyAccessKey();
        return ResponseEntity.ok(result);
    }
}
