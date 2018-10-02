package io.electrica.common.exception;

import javax.servlet.http.HttpServletResponse;

public class ActionForbiddenServiceException extends RestServiceException {

    public ActionForbiddenServiceException(String message) {
        super(HttpServletResponse.SC_FORBIDDEN, message);
    }
}