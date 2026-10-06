package com.example.apirestspringboot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class configSwagger {

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("LIB84 — Book Management API 📕📗📘📙")
                        .version(appVersion)
                        .description("""
                                REST API for managing a digital library system. \
                                Provides full CRUD operations for Books, Authors, Categories, \
                                Suppliers, Users, Profiles and Loans, with pagination, \
                                HATEOAS navigation and centralized error handling.
                                """)
                        .termsOfService("https://swagger.io/terms/")
                        .license(new License().name("MIT").url("https://mit-license.org/"))
                        .contact(new Contact().name("Henrique Maximo").url("https://www.github.com/henrry-maximo")
                                .email("henrrylimadasilva@gmail.com")));
    }

}
