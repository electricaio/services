package io.electrica.stl.repository;

import io.electrica.stl.model.*;
import io.electrica.stl.util.AuthorizationUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class TokenAuthorizationRepositoryTest extends AbstractDatabaseTest {

    @Test
    public void test_find_by_stl_instance_with_success() {

        final AuthorizationType authorizationType = createAuthorizationType(AuthorizationUtils.TOKEN_AUTH);
        final STLType type = createSTLType("Foundation");
        final STL stl = createSTL("Hackerrank API", "0.0.1", type, authorizationType);
        final Long accessKeyId = 1L;
        final STLInstance stlInstance = createSTLInstance(stl, accessKeyId);
        final Authorization authorization = createAuthorization(stlInstance, authorizationType);
        createTokenAuthorization(authorization, "tok_random");

//        method
        final Optional<TokenAuthorization> result = tokenAuthorizationRepository.findBySTLInstance(stlInstance.getId());

        Assert.assertTrue(result.isPresent());

        result.ifPresent(ta -> Assert.assertEquals(authorization.getId(), ta.getAuthorization().getId()));
    }
}
