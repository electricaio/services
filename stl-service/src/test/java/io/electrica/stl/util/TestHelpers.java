package io.electrica.stl.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.common.exception.handler.ErrorResult;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TestHelpers {

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
     * Helper utility that extracts response from the MvcResult class.
     *
     * @param result  - the MvcResult
     * @param classes - corresponding classes that are returned from the endpoint
     *                <p>
     *                If you're method is returning ResponseEntity<String>
     *                then the corresponding classes for this method be only a String.class parameter
     *                <p>
     *                Currently it supports only lists as collection types.
     *                Meaning for response like this: ResponseEntity<List<T>> passed in classes would be
     *                List.class, T.class
     *                <p>
     *                See convertData() if you'd like to add more support.
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
