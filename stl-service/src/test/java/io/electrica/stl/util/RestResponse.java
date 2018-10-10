package io.electrica.stl.util;

import io.electrica.common.exception.handler.ErrorResult;

import java.util.Optional;

/**
 * Wrapper class that returns either successful data of type <ResponseType>
 * or an error of type ErrorResult.class
 */
public final class RestResponse<ResponseType> {

    private static final String MISSING_DATA_ERR_MSG =
            "Missing data - unwrapping might resulted as an error. Try using getError() instead.";

    private static final String MISSING_ERROR_ERR_MSG =
            "Missing error - unwrapping might resulted as success. Try using getData() instead.";

    private Optional<ResponseType> dtoResponse;

    private Optional<ErrorResult> error;

    private int status;

    public RestResponse(Optional<ResponseType> dtoResponse, Optional<ErrorResult> error, int status) {
        this.dtoResponse = dtoResponse;
        this.error = error;
        this.status = status;
    }

    public ResponseType getData() {
        return this.dtoResponse
                .orElseThrow(() -> new IllegalStateException(MISSING_DATA_ERR_MSG));
    }

    public ErrorResult getError() {
        return this.error
                .orElseThrow(() -> new IllegalArgumentException(MISSING_ERROR_ERR_MSG));
    }

    public int getStatus() {
        return this.status;
    }
}
