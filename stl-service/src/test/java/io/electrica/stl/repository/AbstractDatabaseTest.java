package io.electrica.stl.repository;

import io.electrica.STLServiceApplicationTest;
import io.electrica.stl.util.Fixture;

import javax.inject.Inject;

public abstract class AbstractDatabaseTest extends STLServiceApplicationTest implements Fixture {

    @Inject
    protected STLTypeRepository stlTypeRepository;

    @Inject
    protected STLRepository stlRepository;

    @Inject
    protected AuthorizationTypeRepository authorizationTypeRepository;

    @Override
    public AuthorizationTypeRepository getAuthorizationTypeRepository() {
        return authorizationTypeRepository;
    }

    @Override
    public STLTypeRepository getSTLTypeRepository() {
        return stlTypeRepository;
    }
}
