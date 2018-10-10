package io.electrica.common.exception.handler;

import io.electrica.common.EnvironmentType;
import io.electrica.common.exception.ActionForbiddenServiceException;
import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.common.exception.EntityNotFoundServiceException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ServiceExceptionHandlerTest {

    private ServiceExceptionHandler testServiceExceptionHandler;
    private ServiceExceptionHandler prodServiceExceptionHandler;

    @Before
    public void setUp() throws Exception {
        List<CustomExceptionHandler> exceptionHandlers = Arrays.asList(
                new DefaultExceptionHandler(),
                new RestServiceExceptionHandler()
        );
        testServiceExceptionHandler = new ServiceExceptionHandler(exceptionHandlers, EnvironmentType.Test);
        prodServiceExceptionHandler = new ServiceExceptionHandler(exceptionHandlers, EnvironmentType.Production);
    }

    @Test
    public void testBadRequestServiceException() {
        String msg = "br";
        BadRequestServiceException exception = new BadRequestServiceException(msg);

        assertRestServiceExceptionHandler(msg, HttpServletResponse.SC_BAD_REQUEST, exception);
    }

    @Test
    public void testActionForbiddenServiceException() {
        String msg = "af";
        ActionForbiddenServiceException exception = new ActionForbiddenServiceException(msg);

        assertRestServiceExceptionHandler(msg, HttpServletResponse.SC_FORBIDDEN, exception);
    }

    @Test
    public void testEntityNotFoundServiceException() {
        String msg = "enf";
        EntityNotFoundServiceException exception = new EntityNotFoundServiceException(msg);

        assertRestServiceExceptionHandler(msg, HttpServletResponse.SC_NOT_FOUND, exception);
    }

    @Test
    public void testRuntimeException() {
        String msg = "rt";
        RuntimeException exception = new RuntimeException(msg);

        assertRestServiceExceptionHandler(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception);
    }

    private void assertRestServiceExceptionHandler(String expectedMessage, int expectedStatus, Exception exception) {
        HttpServletRequest request = mock(HttpServletRequest.class);

        ErrorResult result = testServiceExceptionHandler.handleControllerException(request, exception).getBody();
        assertNotNull(result);
        assertEquals(expectedStatus, result.getHttpStatus());
        assertEquals(expectedMessage, result.getMessage());
        assertNull(result.getErrorCode());
        assertNotNull(result.getStackTrace());

        result = prodServiceExceptionHandler.handleControllerException(request, exception).getBody();
        assertNotNull(result);
        assertEquals(expectedStatus, result.getHttpStatus());
        assertEquals(expectedMessage, result.getMessage());
        assertNull(result.getErrorCode());
        assertNull(result.getStackTrace());
    }
}
