package io.electrica.stl.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.stl.model.*;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.rest.dto.AuthorizationData;
import io.electrica.stl.util.Fixture;
import org.junit.Test;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import java.util.List;

import static io.electrica.stl.util.AuthorizationUtils.TOKEN_AUTH;
import static org.junit.Assert.*;

public class STLInstanceServiceTest extends AbstractDatabaseTest implements Fixture {

    @Inject
    private STLInstanceService stlInstanceService;

    @Test
    public void test_create_stl_instance_with_success() {
//        setup
        final AuthorizationType authorizationType = createAuthorizationType(TOKEN_AUTH);
        final STLType type = createSTLType("Foundation");
        final STL stl = createSTL("Hackerrank", "v.1.0.0", type, authorizationType);

        final AuthorizationData data = new AuthorizationData();
        data.setToken("tok_random");

        final Long accessKeyId = 1L;

        // method
        stlInstanceService.create(stl.getId(), accessKeyId, data);

        final List<STLInstance> results = stlInstanceRepository.findAll();
        final STLInstance actual = results.get(0);

        assertEquals(accessKeyId, actual.getAccessKeyId());
        assertEquals(stl.getId(), actual.getStl().getId());

        final TokenAuthorization tokenAuthorization = tokenAuthorizationRepository.findBySTLInstance(actual.getId()).get();
        assertEquals(data.getToken(), tokenAuthorization.getTokenHash());
    }

    @Test(expected = BadRequestServiceException.class)
    public void test_create_stl_instance_with_missing_auth_data() {
//        setup
        final AuthorizationType authorizationType = createAuthorizationType(TOKEN_AUTH);
        final STLType type = createSTLType("Foundation");
        final STL stl = createSTL("Hackerrank", "v.1.0.0", type, authorizationType);

        final AuthorizationData data = new AuthorizationData();

        final Long accessKeyId = 1L;

        // method
        stlInstanceService.create(stl.getId(), accessKeyId, data);
    }

    @Test(expected = BadRequestServiceException.class)
    public void test_create_stl_instance_with_same_access_key() {
//        setup
        final AuthorizationType authorizationType = createAuthorizationType(TOKEN_AUTH);
        final STLType type = createSTLType("Foundation");
        final STL stl = createSTL("Hackerrank", "v.1.0.0", type, authorizationType);

        final AuthorizationData authorizationData = new AuthorizationData();

        final Long accessKeyId = 1L;

        createSTLInstance(stl, accessKeyId);

//        method
        stlInstanceService.create(stl.getId(), accessKeyId, authorizationData);
    }

    @Test(expected = EntityNotFoundException.class)
    public void test_create_stl_instance_for_non_existing_stl() {
//        setup
        final Long stlId = 1L;
        final Long accessKeyId = 1L;

        final AuthorizationData authorizationData = new AuthorizationData();

//        method
        stlInstanceService.create(stlId, accessKeyId, authorizationData);
    }

}
