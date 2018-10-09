package io.electrica.stl.repository;

import io.electrica.STLServiceApplication;
import io.electrica.stl.util.Fixture;
import io.electrica.stl.util.TestProfileResolver;
import org.junit.Before;
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
    protected STLInstanceRepository stlInstanceRepository;

    @Inject
    protected AuthorizationTypeRepository authorizationTypeRepository;

    @Inject
    protected BasicAuthorizationRepository basicAuthorizationRepository;

    @Inject
    protected AwsIamAuthorizationRepository awsIamAuthorizationRepository;

    @Inject
    protected TokenAuthorizationRepository tokenAuthorizationRepository;

    @Inject
    private AuthorizationRepository authorizationRepository;

    @Override
    public AuthorizationRepository getAuthorizationRepository() {
        return authorizationRepository;
    }

    @Override
    public AuthorizationTypeRepository getAuthorizationTypeRepository() {
        return authorizationTypeRepository;
    }

    @Override
    public TokenAuthorizationRepository getTokenAuthorizationRepository() {
        return tokenAuthorizationRepository;
    }

    @Override
    public STLRepository getSTLRepository() {
        return stlRepository;
    }

    @Override
    public STLTypeRepository getSTLTypeRepository() {
        return stlTypeRepository;
    }

    public STLInstanceRepository getSTLInstanceRepository() {
        return stlInstanceRepository;
    }
}
