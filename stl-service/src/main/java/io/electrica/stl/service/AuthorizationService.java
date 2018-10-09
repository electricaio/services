package io.electrica.stl.service;

import io.electrica.stl.model.*;
import io.electrica.stl.rest.dto.AuthorizationData;

public interface AuthorizationService {

    BasicAuthorization createBasicAuth(AuthorizationType type,
                                       STLInstance stlInstance,
                                       AuthorizationData data);

    AwsIamAuthorization createAwsIamAuth(AuthorizationType type,
                                         STLInstance stlInstance,
                                         AuthorizationData data);

    TokenAuthorization createTokenAuth(AuthorizationType type,
                                       STLInstance stlInstance,
                                       AuthorizationData data);
}
