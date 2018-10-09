package io.electrica.stl.util;

import io.electrica.stl.model.*;
import io.electrica.stl.repository.*;

public interface Fixture {

    default AuthorizationType createAuthorizationType(String name) {
        final AuthorizationType model = new AuthorizationType();
        model.setName(name);
        return getAuthorizationTypeRepository().save(model);
    }

    default STL createSTL(String name, String version, STLType type, AuthorizationType authorizationType) {
        final STL model = new STL();
        model.setName(name);
        model.setVersion(version);
        model.setType(type);
        model.setNamespace("stl.test");
        model.setErn("ern://stl.test");
        model.setAuthorizationType(authorizationType);
        return getSTLRepository().save(model);
    }

    default STLType createSTLType(String name) {
        final STLType model = new STLType();
        model.setName(name);
        return getSTLTypeRepository().save(model);
    }

    default STLInstance createSTLInstance(STL stl, Long accessKeyId) {
        final STLInstance model = new STLInstance();
        model.setStl(stl);
        model.setAccessKeyId(accessKeyId);
        return getSTLInstanceRepository().save(model);
    }

    default Authorization createAuthorization(STLInstance stlInstance, AuthorizationType type) {
        final Authorization model = new Authorization();
        model.setStlInstance(stlInstance);
        model.setType(type);
        return getAuthorizationRepository().save(model);
    }

    default TokenAuthorization createTokenAuthorization(Authorization authorization, String token){
        final TokenAuthorization model = new TokenAuthorization();
        model.setTokenHash(token);
        model.setAuthorization(authorization);
        return getTokenAuthorizationRepository().save(model);
    }

    AuthorizationRepository getAuthorizationRepository();

    AuthorizationTypeRepository getAuthorizationTypeRepository();

    TokenAuthorizationRepository getTokenAuthorizationRepository();

    STLRepository getSTLRepository();

    STLTypeRepository getSTLTypeRepository();

    STLInstanceRepository getSTLInstanceRepository();
}
