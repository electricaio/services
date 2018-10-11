package io.electrica.stl.util;

import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STLType;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import io.electrica.stl.repository.AuthorizationTypeRepository;
import io.electrica.stl.repository.STLTypeRepository;

public interface Fixture {

    default AuthorizationType createAuthorizationType(AuthorizationTypeName name) {
        final AuthorizationType model = new AuthorizationType();
        model.setName(name);
        return getAuthorizationTypeRepository().saveAndFlush(model);
    }

    default STLType createSTLType(String name) {
        final STLType model = new STLType();
        model.setName(name);
        return getSTLTypeRepository().saveAndFlush(model);
    }

    AuthorizationTypeRepository getAuthorizationTypeRepository();

    STLTypeRepository getSTLTypeRepository();
}
