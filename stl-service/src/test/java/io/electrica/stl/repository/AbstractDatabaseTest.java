package io.electrica.stl.repository;

import io.electrica.STLServiceApplication;
import io.electrica.stl.TestProfileResolver;
import io.electrica.stl.util.Fixture;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
@SpringBootTest(classes = STLServiceApplication.class)
@Transactional
public abstract class AbstractDatabaseTest implements Fixture {

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
