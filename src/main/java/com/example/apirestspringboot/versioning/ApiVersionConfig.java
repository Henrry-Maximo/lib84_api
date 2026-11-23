package com.example.apirestspringboot.versioning;

import com.example.apirestspringboot.RateLimitingInterceptor;
import com.example.apirestspringboot.idempotency.IdempotencyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    public static final String HEADER = "X-API-Version";

    private final RateLimitingInterceptor rateLimitingInterceptor;
    private final IdempotencyInterceptor idempotencyInterceptor;

    public ApiVersionConfig(RateLimitingInterceptor rateLimitingInterceptor, IdempotencyInterceptor idempotencyInterceptor) {
        this.rateLimitingInterceptor = rateLimitingInterceptor;
        this.idempotencyInterceptor = idempotencyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor());
        registry.addInterceptor(rateLimitingInterceptor);
        registry.addInterceptor(idempotencyInterceptor);
    }

// sobreescreve método nativo do spring boot para manipulação da request/response
    // insere a chave "X-API-Version" com o valor padrão de 1
    // configura uma única vez e usa o atributo direto nas anotações das controllers
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader(HEADER)
                .addSupportedVersions("1", "2")
                .setDefaultVersion("1");
    }
}
