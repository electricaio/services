package io.electrica.common.helper;

import io.electrica.common.rest.PathConstants;

import javax.servlet.http.HttpServletRequest;

public final class RequestHelper {

    private RequestHelper() {
    }

    public static String getFullPath(HttpServletRequest request) {
        return request.getContextPath() + request.getServletPath();
    }

    public static boolean isSecuredResource(HttpServletRequest request) {
        String path = getFullPath(request);
        return !isAuthPath(path) &&
                !isHealthPath(path) &&
                !isSwaggerApiDoc(path) &&
                !isPublicApi(path) &&
                !isPrivateApi(path);
    }

    public static boolean isPublicApi(String path) {
        return path.startsWith(PathConstants.PUBLIC);
    }

    public static boolean isPrivateApi(String path) {
        return path.startsWith(PathConstants.PRIVATE);
    }

    public static boolean isSwaggerApiDoc(String path) {
        return path.startsWith("/v2/api-docs");
    }

    public static boolean isAuthPath(String path) {
        return path.startsWith("/oauth");
    }

    public static boolean isHealthPath(String path) {
        return path.startsWith("/health");
    }

}
