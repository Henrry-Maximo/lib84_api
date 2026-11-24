package com.example.apirestspringboot.versioning;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class ApiVersionInterceptor implements HandlerInterceptor {

    private static final Set<String> SUPPORTED = Set.of("1", "2");

    // interceptor para rodar antes da controller
    // se vier com o API-KEY-Version diferente de 1 ou 2, irá retornar 400
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String version = request.getHeader(ApiVersionConfig.HEADER);
        
        // Swagger UI envia o header como query param quando configurado via Parameters
        if (version == null) {
            version = request.getParameter(ApiVersionConfig.HEADER);
        }

        if (version != null && !SUPPORTED.contains(version)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"unsupported API version. Supported versions: 1, 2.\"}");
            return false;
        }

        return true;
    }
}
