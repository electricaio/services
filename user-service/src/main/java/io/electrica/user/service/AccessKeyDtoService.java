package io.electrica.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.model.AccessKey;

@Component
public class AccessKeyDtoService extends AbstractDtoService<AccessKey, AccessKeyDto> {

    private final AccessKeyService accessKeyService;

    public AccessKeyDtoService(AccessKeyService accessKeyService) {
        this.accessKeyService = accessKeyService;
    }

    public List<AccessKeyDto> findAllNonArchivedByUser(Long userId) {
        return accessKeyService.findAllNonArchivedByUser(userId).
                stream().
                map(e -> {
                    e.setAccessKey(null);
                    return toDto(e);
                }).
                collect(Collectors.toList());
    }

    public AccessKeyDto findByKeyAndUser(Long accessKeyId, Long userId) {
        return toDto(accessKeyService.findByKeyAndUser(accessKeyId, userId));
    }

    @Override
    public AccessKeyDto create(AccessKeyDto persistentDto) {
        AccessKeyDto result = super.create(persistentDto);
        //We should display Access Key on demand as a separate call
        result.setAccessKey(null);
        return result;
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
