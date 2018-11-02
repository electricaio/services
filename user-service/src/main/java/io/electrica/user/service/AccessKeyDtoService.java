package io.electrica.user.service;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.model.AccessKey;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccessKeyDtoService extends AbstractDtoService<AccessKey, CreateAccessKeyDto, AccessKeyDto> {

    private final AccessKeyService accessKeyService;
    private final IdentityContextHolder identityContextHolder;

    public AccessKeyDtoService(AccessKeyService accessKeyService, IdentityContextHolder identityContextHolder) {
        this.accessKeyService = accessKeyService;
        this.identityContextHolder = identityContextHolder;
    }

    public List<AccessKeyDto> findByUser(Long userId) {
        return toDto(accessKeyService.findByUser(userId));
    }

    public FullAccessKeyDto findByKey(Long accessKeyId) {
        AccessKey entity = accessKeyService.findById(accessKeyId, true);
        return mapper.map(entity, FullAccessKeyDto.class);
    }

    public AccessKeyDto refreshKey(long accessKeyId) {

        return toDto(accessKeyService.refreshKey(accessKeyId));
    }

    public Boolean validateMyAccessKeyById(long accessKeyId) {
        Identity identity = identityContextHolder.getIdentity();
        return accessKeyService.validateUserAccessKeyById(accessKeyId, identity.getUserId());
    }

    public Boolean validateMyAccessKey() {
        Identity identity = identityContextHolder.getIdentity();
        return accessKeyService.validateUserAccessKeyByJti(identity.getTokenJti(), identity.getUserId());
    }

    @Override
    protected AbstractService<AccessKey> getService() {
        return accessKeyService;
    }

    @Override
    protected Class<AccessKey> getEntityClass() {
        return AccessKey.class;
    }

    @Override
    protected Class<AccessKeyDto> getDtoClass() {
        return AccessKeyDto.class;
    }

}
