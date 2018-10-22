package io.electrica.connector.hub.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.common.exception.handler.ErrorResult;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TestApiHelper {


    ObjectMapper getObjectMapper();

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    default byte[] convertObjectToJsonBytes(Object object) throws IOException {
        final ObjectMapper mapper = getObjectMapper();
        return mapper.writeValueAsBytes(object);
    }


    /**
     * Helper method that extracts response T provided in Response<T> from the MvcResult class.
     *
     * It takes the list of classes defined in T as a sequence
     * and constructs the wrapper of type RestResponse<T>
     *
     * Usage:
     *
     *  final MvcResult mvcResult = mockMvc
     *           .perform(
     *               get(PathConstants.V1 + "/stl/list"))
     *           .andReturn();
     *
     *  final RestResponse<List<STLDto>> response = fromMvcResult(mvcResult, List.class, STLDto.class);
     *
     *  ... response.getData()
     *
     *  Currently it only supports List.class as for collection types. You can extend the converter with more options
     *  under convertData() method
     */
    default <ResponseType> RestResponse<ResponseType> fromMvcResult(MvcResult result,
                                                                    Class<?>... classes) throws Exception {
        if (classes.length == 0) {
            throw new IllegalArgumentException("Missing arguments - " +
                    "there needs to be at least one class type passed for parameter <Class<?> classes>.");
        }
        final String rawData = result.getResponse().getContentAsString();

        if (result.getResponse().getStatus() >= 200 && result.getResponse().getStatus() < 300) {
            return new RestResponse<>(
                    convertData(rawData, classes),
                    Optional.empty(),
                    result.getResponse().getStatus()
            );
        }
        return new RestResponse<>(
                Optional.empty(),
                convertData(rawData, ErrorResult.class),
                result.getResponse().getStatus()
        );
    }

    /**
     * Given the list of classes, it returns a corresponding type wrapped in as Optional.
     */
    default <ResponseType> Optional<ResponseType> convertData(String rawData,
                                                              Class<?>... classes) throws Exception {
        if (classes[0].equals(Void.class)) {
            return Optional.empty();
        } else if (classes[0] == List.class) {
            final JavaType type = getObjectMapper().getTypeFactory().constructCollectionType(List.class, classes[1]);
            return Optional.of(getObjectMapper().readValue(rawData, type));
        }
        return Optional.of((ResponseType) getObjectMapper().readValue(rawData, classes[0]));
    }
}
