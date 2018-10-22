package io.electrica.stl.repository;

import io.electrica.STLServiceApplicationTest;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STLType;
import io.electrica.stl.util.Fixture;
import org.junit.Before;

import javax.inject.Inject;
import java.util.List;

public abstract class AbstractDatabaseTest extends STLServiceApplicationTest implements Fixture {

    @Inject
    protected STLInstanceRepository stlInstanceRepository;

    @Inject
    protected STLTypeRepository stlTypeRepository;

    @Inject
    protected AuthorizationTypeRepository authorizationTypeRepository;

    @Inject
    protected STLRepository stlRepository;

    protected List<AuthorizationType> authorizationTypes;

    protected List<STLType> stlTypes;

    @Before
    public void setup() {
        stlTypes = stlTypeRepository.findAll();
        authorizationTypes = authorizationTypeRepository.findAll();
    }

    @Override
    public STLTypeRepository getSTLTypeRepository() {
        return stlTypeRepository;
    }

    @Override
    public AuthorizationTypeRepository getAuthorizationTypeRepository() {
        return authorizationTypeRepository;
    }
}
