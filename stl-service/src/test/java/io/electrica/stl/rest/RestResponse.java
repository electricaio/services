package io.electrica.stl.rest;

import java.util.Optional;

/**
 * Wrapper class that returns either successful data or an error.
 * */
public final class RestResponse<ResponseType> {

    private static final String MISSING_DATA_ERR_MSG =
            "Missing data - unwrapping might resulted as an error. Try using getError() instead.";

    private static final String MISSING_ERROR_ERR_MSG =
            "Missing error - unwrapping might resulted as success. Try using getData() instead.";

    private Optional<ResponseType> dtoResponse;

    // will be changed with proper class wrapper for errors
    private Optional<String> error;

    private int status;

    public RestResponse(Optional<ResponseType> dtoResponse, Optional<String> error, int status) {
        this.dtoResponse = dtoResponse;
        this.error = error;
        this.status = status;
    }

    public ResponseType getData() {
        return this.dtoResponse
                .orElseThrow(() -> new IllegalStateException(MISSING_DATA_ERR_MSG));
    }

    public String getError() {
        return this.error
                .orElseThrow(() -> new IllegalArgumentException(MISSING_ERROR_ERR_MSG));
    }
    public int getStatus() {
        return this.status;
    }
}
