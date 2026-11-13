package com.example.apirestspringboot.versioning;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**",
                        "/docs",
                        "/docs.html",
                        "/terms",
                        "/h2-console/**",
                        "/",
                        "/*.css",
                        "/*.js",
                        "/*.png",
                        "/*.webp",
                        "/*.gif",
                        "/*.mp4"
                );
    }
}
