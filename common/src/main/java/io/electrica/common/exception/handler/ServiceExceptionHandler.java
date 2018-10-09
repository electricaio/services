package io.electrica.common.exception.handler;

import io.electrica.common.EnvironmentType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String HTTP_STATUS_FIELD_NAME = "http_status";
    private static final Logger log = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    private final Map<Class<? extends Exception>, CustomExceptionHandler> handlersMap = new HashMap<>();

    private final EnvironmentType environmentType;

    @Inject
    public ServiceExceptionHandler(
            List<CustomExceptionHandler> customExceptionHandlers,
            EnvironmentType environmentType
    ) {
        this.environmentType = environmentType;
        for (CustomExceptionHandler handler : customExceptionHandlers) {
            for (Class<? extends Exception> exceptionClass : handler.getSupportedExceptions()) {
                if (handlersMap.put(exceptionClass, handler) != null) {
                    String msg = String.format("Handler for %s already registered", exceptionClass.getName());
                    throw new IllegalStateException(msg);
                }
            }
        }
    }

    private static int inheritanceDistance(Class<? extends Exception> parent, Class<?> son) {
        int distance = 0;
        Class<?> test = son.getSuperclass();
        while (!parent.equals(test)) {
            distance++;
            test = test.getSuperclass();
        }

        return distance;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleControllerException(HttpServletRequest request, Exception e) {
        CustomExceptionHandler handler = getExceptionHandler(e.getClass());

        ErrorResult errorResult = handler.handle(e);

        if (environmentType.isSafe()) {
            errorResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        }

        MDC.put(HTTP_STATUS_FIELD_NAME, String.valueOf(errorResult.getHttpStatus()));
        try {
            log.error(String.format(
                    "%s handle %s",
                    handler.getClass().getSimpleName()
                    , e.getClass().getSimpleName()
            ), e);
        } finally {
            MDC.remove(HTTP_STATUS_FIELD_NAME);
        }

        return ResponseEntity.status(errorResult.getHttpStatus()).body(errorResult);
    }

    private CustomExceptionHandler getExceptionHandler(Class<? extends Exception> exceptionClass) {
        CustomExceptionHandler handler = handlersMap.get(exceptionClass);
        if (handler == null) {
            handler = handlersMap.get(findClosestParentException(exceptionClass));
        }
        return handler;
    }

    private Class<? extends Exception> findClosestParentException(Class<? extends Exception> exceptionClass) {
        List<Class<? extends Exception>> parentClasses = handlersMap.keySet().stream()
                .filter(clazz -> clazz.isAssignableFrom(exceptionClass))
                .collect(Collectors.toList());

        Map<? extends Class<? extends Exception>, Integer> inheritanceMap = parentClasses.stream()
                .collect(Collectors.toMap(
                        clazz -> clazz,
                        clazz -> inheritanceDistance(clazz, exceptionClass)
                ));

        return inheritanceMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0)
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .orElseThrow(IllegalStateException::new)
                .getKey();
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        MDC.put(HTTP_STATUS_FIELD_NAME, status.toString());
        try {
            log.warn("Spring handled error:", ex);
            return super.handleExceptionInternal(ex, body, headers, status, request);
        } finally {
            MDC.remove(HTTP_STATUS_FIELD_NAME);
        }
    }

}
