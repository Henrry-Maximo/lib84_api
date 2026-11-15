package com.example.apirestspringboot.versioning;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    public static final String HEADER = "X-API-Version";

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
