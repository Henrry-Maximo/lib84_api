package com.example.apirestspringboot;

import com.example.apirestspringboot.versioning.ApiVersion;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ConfigSwagger {

    /**
     * Collects all paths whose handler is annotated with @ApiVersion containing the given version,
     * or (for V1) has no @ApiVersion annotation at all.
     */
    private OpenApiCustomizer versionFilter(RequestMappingHandlerMapping mapping, int version) {
        return openApi -> {
            Set<String> includedPaths = mapping.getHandlerMethods().entrySet().stream()
                    .filter(entry -> {
                        HandlerMethod hm = entry.getValue();
                        ApiVersion classAnn = hm.getBeanType().getAnnotation(ApiVersion.class);
                        ApiVersion methodAnn = hm.getMethodAnnotation(ApiVersion.class);
                        int[] versions;
                        if (methodAnn != null) versions = methodAnn.value();
                        else if (classAnn != null) versions = classAnn.value();
                        else versions = new int[]{1}; // unannotated = V1
                        return Arrays.stream(versions).anyMatch(v -> v == version);
                    })
                    .flatMap(entry -> entry.getKey().getPatternValues().stream())
                    .collect(Collectors.toSet());

            if (openApi.getPaths() != null) {
                openApi.getPaths().entrySet().removeIf(e -> !includedPaths.contains(e.getKey()));
            }
        };
    }

    @Bean
    public GroupedOpenApi v1(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("V1 - Stable")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(versionFilter(requestMappingHandlerMapping, 1))
                .build();
    }

    @Bean
    public GroupedOpenApi v2(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("V2 - In Development")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(versionFilter(requestMappingHandlerMapping, 2))
                .build();
    }

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
