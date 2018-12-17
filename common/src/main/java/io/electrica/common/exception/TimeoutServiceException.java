package io.electrica.common.exception;

import javax.servlet.http.HttpServletResponse;

public class TimeoutServiceException extends RestServiceException {

    public TimeoutServiceException() {
        super(HttpServletResponse.SC_GATEWAY_TIMEOUT, "Request timed out");
    }
}
