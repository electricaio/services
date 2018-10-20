package io.electrica.stl.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.stl.model.*;
import io.electrica.stl.repository.AwsIamAuthorizationRepository;
import io.electrica.stl.repository.BasicAuthorizationRepository;
import io.electrica.stl.repository.TokenAuthorizationRepository;
import io.electrica.stl.rest.dto.AuthorizationDto;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationService {


    private final BasicAuthorizationRepository basicAuthorizationRepository;

    private final AwsIamAuthorizationRepository awsIamAuthorizationRepository;

    private final TokenAuthorizationRepository tokenAuthorizationRepository;

    private final AuthorizationRepository authorizationRepository;

    public AuthorizationService(BasicAuthorizationRepository basicAuthorizationRepository,
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

    public BasicAuthorization createBasicAuth(
            AuthorizationType type,
            STLInstance stlInstance,
            AuthorizationDto authorizationDto) {
        if (authorizationDto.getUser() == null || authorizationDto.getPassword() == null) {
            throw new BadRequestServiceException("Missing mandatory fields for basic authorization type.");
        }
        final Authorization authorization = createAuthorization(type, stlInstance);
        final BasicAuthorization model = new BasicAuthorization();
        model.setUserHash(authorizationDto.getUser());
        model.setPasswordHash(authorizationDto.getPassword());
        model.setAuthorization(authorization);
        return basicAuthorizationRepository.save(model);
    }

    public AwsIamAuthorization createAwsIamAuth(AuthorizationType type,
                                                STLInstance stlInstance,
                                                AuthorizationDto data) {
        if (data.getDetails() == null) {
            throw new BadRequestServiceException("Missing mandatory fields for aws iam authorization type.");
        }
        final Authorization authorization = createAuthorization(type, stlInstance);
        final AwsIamAuthorization model = new AwsIamAuthorization();
        model.setAuthorization(authorization);
        model.setDetails(data.getDetails());
        return awsIamAuthorizationRepository.save(model);
    }

    public TokenAuthorization createTokenAuth(AuthorizationType type, STLInstance stlInstance, AuthorizationDto data) {
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
