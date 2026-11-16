package com.example.apirestspringboot;

import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.repository.UserRepository;
import com.example.apirestspringboot.service.RateLimitService;
import com.example.apirestspringboot.service.TokenBucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final UserRepository userRepository;

    public RateLimitingInterceptor(RateLimitService rateLimitService, UserRepository userRepository) {
        this.rateLimitService = rateLimitService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientKey;
        boolean isPremium = false;

        Object userIdAttr = request.getAttribute("user_id");
        if (userIdAttr != null) {
            clientKey = "user_" + userIdAttr;
            try {
                var userOpt = userRepository.findById(UUID.fromString(userIdAttr.toString()));
                if (userOpt.isPresent()) {
                    isPremium = userOpt.get().getPlan() == User.Plan.PREMIUM;
                }
            } catch (Exception ignored) {}
        } else {
            String forwarded = request.getHeader("X-Forwarded-For");
            String ip = (forwarded != null && !forwarded.isBlank())
                    ? forwarded.split(",")[0].trim()
                    : request.getRemoteAddr();
            clientKey = "ip_" + ip;
        }

        TokenBucket bucket = rateLimitService.getBucket(clientKey, isPremium);
        long remaining = bucket.tryConsume(1);

        response.setHeader("X-RateLimit-Limit",     String.valueOf(bucket.getCapacity()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));

        if (remaining < 0) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "5");
            response.getWriter().write("{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}");
            return false;
        }

        return true;
    }
}
