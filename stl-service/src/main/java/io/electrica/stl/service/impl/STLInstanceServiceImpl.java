package io.electrica.stl.service.impl;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLInstance;
import io.electrica.stl.repository.STLInstanceRepository;
import io.electrica.stl.repository.STLRepository;
import io.electrica.stl.rest.dto.AuthorizationData;
import io.electrica.stl.service.AuthorizationService;
import io.electrica.stl.service.STLInstanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static io.electrica.stl.util.AuthorizationUtils.*;

@Transactional
@Service
public class STLInstanceServiceImpl implements STLInstanceService {

    private STLInstanceRepository stlInstanceRepository;

    private STLRepository stlRepository;

    private AuthorizationService authorizationService;

    public STLInstanceServiceImpl(STLInstanceRepository stlInstanceRepository,
                                  STLRepository stlRepository,
                                  AuthorizationService authorizationService) {
        this.stlInstanceRepository = stlInstanceRepository;
        this.stlRepository = stlRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public STLInstance create(Long stlId, Long accessKeyId, AuthorizationData data) {

        final STL stl = stlRepository.findById(stlId)
                .orElseThrow(EntityNotFoundException::new);

        if (stlInstanceRepository.exists(stlId, accessKeyId)) {
            throw new BadRequestServiceException("STL and access key combination must be unique");
        }

        final STLInstance stlInstance = new STLInstance();
        stlInstance.setStl(stl);
        stlInstance.setAccessKeyId(accessKeyId);
        stlInstanceRepository.save(stlInstance);

        final AuthorizationType type = stl.getAuthorizationType();
        if (isBasicAuthorization(type)) {
            authorizationService.createBasicAuth(type, stlInstance, data);
        }

        if (isAwsIamAuthorization(type)) {
            authorizationService.createAwsIamAuth(type, stlInstance, data);
        }

        if (isTokenAuthorization(type)) {
            authorizationService.createTokenAuth(type, stlInstance, data);
        }

        // todo add event service and push message...

        return stlInstance;
    }
}
