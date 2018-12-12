package io.electrica.common.exception.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Component
public class CustomAccessDeniedHandler implements CustomExceptionHandler {

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(AccessDeniedException.class);
    }

    @Override
    public ErrorResult handle(Exception exception) {
        return new ErrorResult(
                exception.getMessage(),
                HttpServletResponse.SC_FORBIDDEN
        );
    }
}
