package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.CreateAccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.model.AccessKey;
import io.electrica.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public FullAccessKeyDto findByKeyAndUser(Long accessKeyId, Long userId) {
        AccessKey entity = accessKeyService.findByIdAndUser(accessKeyId, userId);
        return mapper.map(entity, FullAccessKeyDto.class);
    }

    public FullAccessKeyDto refreshKey(long accessKeyId) {

        return mapper.map(accessKeyService.refreshKey(accessKeyId), FullAccessKeyDto.class);
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
