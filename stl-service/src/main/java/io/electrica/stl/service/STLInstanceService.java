package io.electrica.stl.service;

import io.electrica.stl.model.STLInstance;
import io.electrica.stl.rest.dto.AuthorizationData;

public interface STLInstanceService {

    STLInstance create(Long stlId, Long accessKeyId, AuthorizationData data);
}
