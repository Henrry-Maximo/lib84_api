package com.example.apirestspringboot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigSwagger {

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("LIB84 API RESTful - Books Management")
                        .version(appVersion)
                        .description("""
                                REST API for managing a digital library: books, authors, categories,
                                suppliers, users, profiles and loans.
                                
                                **Features**
                                - Full CRUD for every resource, with Bean Validation on request bodies
                                - Paginated listings (10 items per page by default) through `page` and `size`
                                - Custom queries: users by email, authors by name, categories by title,
                                  suppliers by name, books by category, loans by user, profile by user id
                                - Hypermedia responses with Spring HATEOAS in HAL format
                                      (`application/hal+json`), with links such as `self`, `update` and `delete`
                                - Centralized error handling and appropriate HTTP status codes
                                """)
                        .termsOfService("/terms")
                        .license(new License().name("MIT").url("https://mit-license.org/"))
                        .contact(new Contact().name("Henrique Maximo").url("https://www.github.com/henrry-maximo")
                                .email("henrrylimadasilva@gmail.com")));
    }

}
