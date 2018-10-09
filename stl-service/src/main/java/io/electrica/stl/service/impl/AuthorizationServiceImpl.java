package io.electrica.stl.service.impl;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.stl.model.*;
import io.electrica.stl.repository.AuthorizationRepository;
import io.electrica.stl.repository.AwsIamAuthorizationRepository;
import io.electrica.stl.repository.BasicAuthorizationRepository;
import io.electrica.stl.repository.TokenAuthorizationRepository;
import io.electrica.stl.rest.dto.AuthorizationData;
import io.electrica.stl.service.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private BasicAuthorizationRepository basicAuthorizationRepository;

    private AwsIamAuthorizationRepository awsIamAuthorizationRepository;

    private TokenAuthorizationRepository tokenAuthorizationRepository;

    private AuthorizationRepository authorizationRepository;

    public AuthorizationServiceImpl(BasicAuthorizationRepository basicAuthorizationRepository,
                                    AwsIamAuthorizationRepository awsIamAuthorizationRepository,
                                    TokenAuthorizationRepository tokenAuthorizationRepository,
                                    AuthorizationRepository authorizationRepository) {
        this.basicAuthorizationRepository = basicAuthorizationRepository;
        this.awsIamAuthorizationRepository = awsIamAuthorizationRepository;
        this.tokenAuthorizationRepository = tokenAuthorizationRepository;
        this.authorizationRepository = authorizationRepository;
    }

    private Authorization createAuthorization(AuthorizationType type, STLInstance stlInstance) {
        final Authorization model = new Authorization();
        model.setType(type);
        model.setStlInstance(stlInstance);
        return authorizationRepository.save(model);
    }

    @Override
    public BasicAuthorization createBasicAuth(AuthorizationType type, STLInstance stlInstance, AuthorizationData data) {
        if (data.getUser() == null || data.getPassword() == null) {
            throw new BadRequestServiceException("Missing mandatory fields for basic authorization type.");
        }
        final Authorization authorization = createAuthorization(type, stlInstance);
        final BasicAuthorization model = new BasicAuthorization();
        model.setUserHash(data.getUser());
        model.setPasswordHash(data.getPassword());
        model.setAuthorization(authorization);
        return basicAuthorizationRepository.save(model);
    }

    @Override
    public AwsIamAuthorization createAwsIamAuth(AuthorizationType type,
                                                STLInstance stlInstance,
                                                AuthorizationData data) {
        if (data.getDetails() == null) {
            throw new BadRequestServiceException("Missing mandatory fields for aws iam authorization type.");
        }
        final Authorization authorization = createAuthorization(type, stlInstance);
        final AwsIamAuthorization model = new AwsIamAuthorization();
        model.setAuthorization(authorization);
        model.setDetails(data.getDetails());
        return awsIamAuthorizationRepository.save(model);
    }

    @Override
    public TokenAuthorization createTokenAuth(AuthorizationType type, STLInstance stlInstance, AuthorizationData data) {
        if (data.getToken() == null) {
            throw new BadRequestServiceException("Missing mandatory fields for token authorization type.");
        }
        final Authorization authorization = createAuthorization(type, stlInstance);
        final TokenAuthorization model = new TokenAuthorization();
        model.setAuthorization(authorization);
        model.setTokenHash(data.getToken());
        return tokenAuthorizationRepository.save(model);
    }
}
