package io.electrica.stl.rest;

import io.electrica.common.rest.PathConstants;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.rest.dto.STLDto;
import io.electrica.stl.util.AuthorizationUtils;
import io.electrica.stl.util.RestResponse;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class STLControllerTest extends AbstractApiTest {

    @Test
    public void test_list_stls_without_data() throws Exception {
//        method
        final MvcResult wrappedResponse = mockMvc.perform(get(PathConstants.V1 + "/stl/list")).andReturn();

//        assert
        final RestResponse<List<STLDto>> response = fromMvcResult(wrappedResponse, List.class, STLDto.class);
        final List<STLDto> result = response.getData();

        assertEquals(0, result.size());
    }

    @Test
    public void test_list_stls_with_existing_one() throws Exception {
        //        setup
        final AuthorizationType authorizationType = createAuthorizationType(AuthorizationUtils.TOKEN_AUTH);
        final STLType type = createSTLType("Foundation");

        final STL expectedStl = createSTL("Hackerrank API", "0.0.1", type, authorizationType);

//        method
        final MvcResult wrappedResponse = mockMvc
                .perform(
                        get(PathConstants.V1 + "/stl/list"))
                .andReturn();

        // assert
        final RestResponse<List<STLDto>> response = fromMvcResult(wrappedResponse, List.class, STLDto.class);
        final List<STLDto> result = response.getData();

        assertEquals(1, result.size());

        final STLDto actual = result.get(0);
        assertEquals(actual.getId(), expectedStl.getId());
        assertEquals(actual.getName(), expectedStl.getName());
        assertEquals(actual.getNamespace(), expectedStl.getNamespace());
        assertEquals(actual.getErn(), expectedStl.getErn());
        assertEquals(actual.getType(), expectedStl.getType().getName());
    }
}
