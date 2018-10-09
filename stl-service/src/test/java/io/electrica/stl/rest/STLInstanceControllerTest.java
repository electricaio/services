package io.electrica.stl.rest;

import io.electrica.common.rest.PathConstants;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLInstance;
import io.electrica.stl.model.STLType;
import io.electrica.stl.rest.dto.AuthorizationData;
import io.electrica.stl.rest.dto.CreateSTLInstance;
import io.electrica.stl.util.RestResponse;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static io.electrica.stl.util.AuthorizationUtils.TOKEN_AUTH;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class STLInstanceControllerTest extends AbstractApiTest {

    @Test
    public void test_create_stl_instance() throws Exception {
//        setup
        final AuthorizationType authorizationType = createAuthorizationType(TOKEN_AUTH);
        final STLType type = createSTLType("Foundation");
        final STL stl = createSTL("Hackerrank", "v.1.0.0", type, authorizationType);

        final CreateSTLInstance request = new CreateSTLInstance();
        final Long accessKeyId = 1L;
        request.setAccessKeyId(1L);
        request.setStlId(stl.getId());
        final AuthorizationData authorizationData = new AuthorizationData();
        authorizationData.setToken("tok_random");
        request.setAuthorizationData(authorizationData);

        final MvcResult wrappedResponse = mockMvc.perform(
                post(PathConstants.V1 + "/stl-instance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(request))
        ).andReturn();

        final RestResponse<Void> response = fromMvcResult(wrappedResponse, Void.class);

        final List<STLInstance> instances = stlInstanceRepository.findAll();

        assertEquals(1, instances.size());

        final STLInstance actual = instances.get(0);
        assertEquals(accessKeyId, actual.getAccessKeyId());
        assertEquals(stl.getId(), actual.getStl().getId());

        assertEquals(200, response.getStatus());
    }
}
