package com.example.apirestspringboot.versioning;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;

public class ApiVersionInterceptor implements HandlerInterceptor {

    static final String HEADER = "X-API-Version";
    static final int DEFAULT_VERSION = 1;
    static final Set<Integer> SUPPORTED_VERSIONS = Set.of(1, 2);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String raw = request.getHeader(HEADER);
        int requestedVersion;

        if (raw == null || raw.isBlank()) {
            requestedVersion = DEFAULT_VERSION;
        } else {
            try {
                requestedVersion = Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                return reject(response, "invalid X-API-Version header value: '" + raw + "'. Expected an integer.");
            }
        }

        if (!SUPPORTED_VERSIONS.contains(requestedVersion)) {
            return reject(response, "unsupported API version: " + requestedVersion + ". Supported versions: " + SUPPORTED_VERSIONS + ".");
        }

        ApiVersion methodAnnotation = handlerMethod.getMethodAnnotation(ApiVersion.class);
        ApiVersion classAnnotation = handlerMethod.getBeanType().getAnnotation(ApiVersion.class);

        int[] supportedByHandler;
        if (methodAnnotation != null) {
            supportedByHandler = methodAnnotation.value();
        } else if (classAnnotation != null) {
            supportedByHandler = classAnnotation.value();
        } else {
            supportedByHandler = new int[]{DEFAULT_VERSION};
        }

        boolean versionMatches = Arrays.stream(supportedByHandler).anyMatch(v -> v == requestedVersion);

        if (!versionMatches) {
            return reject(response, "this endpoint is not available in API version " + requestedVersion + ".");
        }

        request.setAttribute("apiVersion", requestedVersion);
        return true;
    }

    private boolean reject(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        return false;
    }
}
