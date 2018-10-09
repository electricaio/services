package io.electrica.common.jpa.exception.handler;

import io.electrica.common.exception.handler.CustomExceptionHandler;
import io.electrica.common.exception.handler.ErrorResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Support handling of optimistic locking failure.
 */
@Component
public class ObjectOptimisticLockingExceptionHandler implements CustomExceptionHandler {

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(ObjectOptimisticLockingFailureException.class);
    }

    @Override
    public ErrorResult handle(Exception exception) {
        return new ErrorResult(
                "Outdated object version",
                HttpServletResponse.SC_CONFLICT
        );
    }
}
