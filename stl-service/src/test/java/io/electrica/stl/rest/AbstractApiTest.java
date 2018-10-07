package io.electrica.stl.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.stl.repository.AbstractDatabaseTest;
import org.junit.Before;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public abstract class AbstractApiTest extends AbstractDatabaseTest {

    protected MockMvc mockMvc;

    @Inject
    public ObjectMapper objectMapper;

    @Inject
    private WebApplicationContext context;

    @Before
    public void setup() {
        super.setup();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    /**
     * Helper utility that extracts response from the MvcResult class.
     *
     * @param result - the MvcResult
     * @param classes - corresponding classes that are returned from the endpoint
     *
     * If you're method is returning ResponseEntity<String>
     * then the corresponding classes for this method be only a String.class parameter
     *
     * Currently it supports only lists as collection types.
     * Meaning for response like this: ResponseEntity<List<T>> passed in classes would be
     *   List.class, T.class
     *
     * See convertData() if you'd like to add more support.
     * */
    public <ResponseType> RestResponse<ResponseType> fromMvcResult(MvcResult result,
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
                convertData(rawData, String.class),
                result.getResponse().getStatus()
        );
    }

    /**
     * Given the list of classes, it returns a corresponding type wrapped in as Optional.
     */
    private <ResponseType> Optional<ResponseType> convertData(String rawData,
                                                              Class<?>... classes) throws Exception {
        if (classes[0].equals(Void.class)) {
            return Optional.empty();
        } else if (classes[0] == List.class) {
            final JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, classes[1]);
            return Optional.of(objectMapper.readValue(rawData, type));
        }
        return Optional.of((ResponseType) objectMapper.readValue(rawData, classes[0]));
    }
}
