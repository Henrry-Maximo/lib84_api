package com.example.apirestspringboot;

import com.example.apirestspringboot.providers.JWTProvider;
import com.example.apirestspringboot.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;

    public SecurityFilter(JWTProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    // filtro usado para capturar informações do usuário como id/permissão
    // Intercepta a requisição antes de chegar na controller (interceptor/middleware)
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        SecurityContextHolder.getContext().setAuthentication(null); // garantir que está limpo

        // System.out.println(header);

        if (header != null) {
            var subjectToken = this.jwtProvider.validateToken(header);

            if (subjectToken.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            request.setAttribute("user_id", subjectToken);

            var authorities = userRepository.findById(UUID.fromString(subjectToken))
                    .map(user -> List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                    .orElse(List.of());

            var auth = new UsernamePasswordAuthenticationToken(subjectToken, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // disponibilizar para a controller
        filterChain.doFilter(request, response);
    }
}
