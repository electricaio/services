package io.electrica.user.service;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.model.AccessKey;

@Component
public class AccessKeyDtoService extends AbstractDtoService<AccessKey, AccessKeyDto> {

    @Inject
    private AccessKeyService accessKeyService;

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
