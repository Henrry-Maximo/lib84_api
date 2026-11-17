package com.example.apirestspringboot.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

/*
 * aplica as 3 regras no preHandle e grava a resposta no afterCompletion. Se houver exceção,
 * remove a chave para permitir retry.
 */
@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final String ATTR_KEY = "idempotency_key";

    private final IdempotencyStore store;

    public IdempotencyInterceptor(IdempotencyStore store) {
        this.store = store;
    }

    // rotas que não precisam de Idempotency-Key mesmo sendo POST/PATCH
    private static final java.util.Set<String> IDEMPOTENCY_EXCLUDED = java.util.Set.of(
            "/session",
            "/idempotency-demo/echo"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();

        if (!"2".equals(request.getHeader("X-API-Version"))) return true;
        if (!"POST".equals(method) && !"PATCH".equals(method)) return true;
        if (IDEMPOTENCY_EXCLUDED.contains(request.getRequestURI())) return true;

        String key = request.getHeader("Idempotency-Key");
        if (key == null || key.isBlank()) {
            write(response, 422, "{\"error\":\"Idempotency-Key header is required for POST and PATCH requests.\"}");
            return false;
        }

        // o filtro já envolveu a request — lê o body em cache
        byte[] body = (request instanceof CachedBodyRequestWrapper w)
                ? w.getCachedBody()
                : request.getInputStream().readAllBytes();

        String bodyHash = IdempotencyStore.sha256(body);
        IdempotencyStore.Entry existing = store.get(key);

        if (existing != null) {
            if (existing.state() == IdempotencyStore.State.IN_PROGRESS) {
                write(response, 409, "{\"error\":\"A request with this Idempotency-Key is already being processed.\"}");
                return false;
            }
            if (!existing.bodyHash().equals(bodyHash)) {
                write(response, 422, "{\"error\":\"Idempotency-Key reused with a different request body.\"}");
                return false;
            }
            // replay
            response.setStatus(existing.status());
            response.setContentType("application/json");
            response.setHeader("Idempotent-Replayed", "true");
            response.getWriter().write(existing.body());
            return false;
        }

        if (!store.markInProgress(key, bodyHash)) {
            write(response, 409, "{\"error\":\"A request with this Idempotency-Key is already being processed.\"}");
            return false;
        }

        request.setAttribute(ATTR_KEY, key);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String key = (String) request.getAttribute(ATTR_KEY);
        if (key == null) return;

        if (ex != null) {
            store.remove(key);
            return;
        }

        // lê o body capturado pelo wrapper do filtro
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            String body = new String(wrapper.getContentAsByteArray());
            store.markDone(key, wrapper.getStatus(), body);
            // copyBodyToResponse já é chamado pelo filtro ao final
        }
    }

    private void write(HttpServletResponse response, int status, String json) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
