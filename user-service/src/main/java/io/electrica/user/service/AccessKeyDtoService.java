package io.electrica.user.service;

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

    public AccessKeyDtoService(AccessKeyService accessKeyService) {
        this.accessKeyService = accessKeyService;
    }

    public List<AccessKeyDto> findAllNonArchivedByUser(Long userId) {
        return toDto(accessKeyService.findAllNonArchivedByUser(userId));
    }

    public FullAccessKeyDto findByKey(Long accessKeyId) {
        AccessKey entity = accessKeyService.findById(accessKeyId, false);
        return mapper.map(entity, FullAccessKeyDto.class);
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
