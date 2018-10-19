package io.electrica.user.rest;

import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.service.AccessKeyDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
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
    @PreAuthorize("" +
            "#common.hasPermission('CreateAccessKey') AND " +
            "(" +
            "    #common.isSuperAdmin() OR" +
            "    #common.haveOneOfRoles('OrgUser', 'OrgAdmin') AND #common.isUser(#accessKey.userId)" +
            ")")
    public ResponseEntity<AccessKeyDto> createAccessKey(@RequestBody CreateAccessKeyDto accessKey) {
        AccessKeyDto result = accessKeyDtoService.create(accessKey);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("" +
            "#common.hasPermission('ListAccessKeys') AND " +
            "(" +
            "    #common.isSuperAdmin() OR" +
            "    #common.haveOneOfRoles('OrgUser', 'OrgAdmin') AND #common.isUser(#userId)" +
            ")")
    public ResponseEntity<List<AccessKeyDto>> findAllNonArchivedByUser(@PathVariable Long userId) {
        List<AccessKeyDto> result = accessKeyDtoService.findAllNonArchivedByUser(userId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("" +
            "#common.hasPermission('ReadAccessKey') AND " +
            "(" +
            "    #common.isSuperAdmin() OR" +
            "    #common.haveOneOfRoles('OrgUser', 'OrgAdmin') AND #common.isUser(#userId)" +
            ")")
    public ResponseEntity<FullAccessKeyDto> getAccessKey(@PathVariable Long accessKeyId, @PathVariable Long userId) {
        return ResponseEntity.ok(accessKeyDtoService.findByKeyAndUser(accessKeyId, userId));
    }

}
