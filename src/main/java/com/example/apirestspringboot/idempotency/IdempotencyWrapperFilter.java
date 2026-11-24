package com.example.apirestspringboot.idempotency;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/*
 * @Order(1), roda antes de tudo. Envolve request e response nos wrappers para POST/PATCH na V2. O
 * ContentCachingResponseWrapper captura o body gerado pela controller.
*/

@Component
@Order(1)
public class IdempotencyWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String method = httpReq.getMethod();
        String version = httpReq.getHeader("X-API-Version");

        // só envolve POST/PATCH na V2 — evita overhead nas demais requisições
        if ("2".equals(version) && ("POST".equals(method) || "PATCH".equals(method))) {
            // reler corpo da requisição
            CachedBodyRequestWrapper wrappedReq = new CachedBodyRequestWrapper(httpReq);

            // capturar corpo da resposta antes do envio
            ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(httpRes);

            chain.doFilter(wrappedReq, wrappedRes); // entrega o conteúdo
            wrappedRes.copyBodyToResponse(); // cópia para HTTP original
        } else {
            chain.doFilter(request, response);
        }
    }
}
