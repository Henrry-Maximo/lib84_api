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
                        .title("LIB84 API RESTful - Books & Loans Management")
                        .version(appVersion)
                        .description("""
                                Welcome to the LIB84 RESTful API, a digital library management server 
                                powered by Spring Boot and compliant with the OpenAPI 3.0 specification. 
                                Designed around REST maturity best practices, this API delivers paginated 
                                collections, strict Bean Validation, and HATEOAS hypermedia links in HAL 
                                format. Feel free to explore the endpoints, test requests directly through 
                                this interactive UI, or contribute to the project's continuous improvement 
                                via its open-source repository.
                                
                                **Repository**: https://github.com/Henrry-Maximo/lib84_api
                                
                                ## Getting Started
                                
                                To avoid relational dependency conflicts, follow this sequence when populating the database:
                                
                                1. **Create Profiles & Users** — Set up access profiles and register library members.
                                2. **Create Authors, Categories & Suppliers** — Register the core catalog metadata.
                                3. **Create Books** — Add books linking them to existing authors, categories, and suppliers.
                                4. **Create Loans** — Register book loans associating registered users with available books.
                                
                                ## Features
                                
                                * **Full CRUD Operations:** Complete endpoints for every resource, protected with **Bean Validation** on all request bodies.
                                * **Pagination & Sorting:** Standardized paginated listings (defaulting to `10` items per page) via `page` and `size` query parameters.
                                * **Custom Search Queries:** Dedicated filters to query users by email, authors by name, categories by title, suppliers by name, books by category, loans by user, and profiles by user ID.
                                * **Hypermedia-Driven (HATEOAS):** Responses formatted in **HAL (`application/hal+json`)**, providing dynamic navigation links such as `self`, `update`, and `delete`.
                                * **Standardized Error Handling:** Centralized exception handling returning clear, structured error payloads with appropriate HTTP status codes.
                                """)
                        .termsOfService("/terms")
                        .license(new License().name("MIT").url("https://mit-license.org/"))
                        .contact(new Contact().name("Henrique Maximo").url("https://www.github.com/henrry-maximo")
                                .email("henrrylimadasilva@gmail.com")));
    }

}
