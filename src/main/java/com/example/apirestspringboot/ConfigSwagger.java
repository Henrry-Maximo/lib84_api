package com.example.apirestspringboot;

import com.example.apirestspringboot.versioning.ApiVersionConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;


@Configuration
public class ConfigSwagger {

    private static final String BEARER_SCHEME = "bearerAuth";

    /**
     * Injects a standard 500 response into every operation that does not already declare one.
     * Centralizes the error contract so individual controllers don't need to repeat it.
     */
    private void add500ToAllOperations(io.swagger.v3.oas.models.OpenAPI openApi) {
        if (openApi.getPaths() == null) return;
        var schema = new io.swagger.v3.oas.models.media.ObjectSchema()
                .addProperty("timestamp", new io.swagger.v3.oas.models.media.StringSchema().example("2025-01-01T00:00:00Z"))
                .addProperty("status", new io.swagger.v3.oas.models.media.IntegerSchema().example(500))
                .addProperty("error", new io.swagger.v3.oas.models.media.StringSchema().example("Internal Server Error"))
                .addProperty("message", new io.swagger.v3.oas.models.media.StringSchema().example("an unexpected error occurred. Please try again later."))
                .addProperty("path", new io.swagger.v3.oas.models.media.StringSchema().example("/authors"));
        var mediaType = new io.swagger.v3.oas.models.media.MediaType().schema(schema);
        var content = new io.swagger.v3.oas.models.media.Content()
                .addMediaType("application/json", mediaType);
        var response = new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Unexpected internal server error")
                .content(content);
        openApi.getPaths().values().forEach(item ->
                item.readOperations().forEach(op -> {
                    if (op.getResponses() != null && !op.getResponses().containsKey("500")) {
                        op.getResponses().addApiResponse("500", response);
                    }
                })
        );
    }

    /**
     * Swagger document for V1.
     *
     * addOpenApiMethodFilter: includes a handler method in this document when its @RequestMapping
     * has no version attribute (shared across all versions) or version = "1" (V1-only).
     *
     * addOperationCustomizer: finds the X-API-Version header parameter that Spring MVC exposes
     * automatically and locks its value to "1", so the Swagger UI pre-fills it and prevents
     * the user from typing a different version while browsing the V1 document.
     *
     * addOpenApiCustomizer: strips the Bearer security scheme (V1 is public) and appends the
     * standard 500 response to every operation.
     */
    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("V1 - Stable")
                .pathsToMatch("/**")
                .addOpenApiMethodFilter(method -> {
                    RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    return mapping == null || mapping.version().isEmpty() || mapping.version().equals("1");
                })
                .addOperationCustomizer((operation, handlerMethod) -> {
                    if (operation.getParameters() != null) {
                        operation.getParameters().stream()
                                .filter(p -> ApiVersionConfig.HEADER.equals(p.getName()))
                                .forEach(p -> p.schema(new StringSchema()._enum(java.util.List.of("1"))._default("1")));
                    }
                    return operation;
                })
                .addOpenApiCustomizer(openApi -> {
                    openApi.getComponents().setSecuritySchemes(null);
                    openApi.setSecurity(null);
                    openApi.getInfo().setVersion("1.0.0");
                    add500ToAllOperations(openApi);
                })
                .build();
    }

    /**
     * Swagger document for V2.
     *
     * addOpenApiMethodFilter: includes only handler methods explicitly annotated with version = "2".
     * Unlike V1, methods without a version are excluded — V2 is built incrementally and only
     * exposes what has been intentionally ported or created for this version.
     *
     * addOperationCustomizer: same header-locking strategy as V1, but pre-fills "2".
     *
     * addOpenApiCustomizer: appends the Bearer JWT security requirement to every operation
     * (all V2 routes require authentication) and adds the standard 500 response.
     */
    @Bean
    public GroupedOpenApi v2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("V2 - In Development")
                .pathsToMatch("/**")
                .addOpenApiMethodFilter(method -> {
                    RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    return mapping != null && mapping.version().equals("2");
                })
                .addOperationCustomizer((operation, handlerMethod) -> {
                    if (operation.getParameters() != null) {
                        operation.getParameters().stream()
                                .filter(p -> ApiVersionConfig.HEADER.equals(p.getName()))
                                .forEach(p -> p.schema(new StringSchema()._enum(java.util.List.of("2"))._default("2")));
                    }

                    // adiciona Idempotency-Key nos POST e PATCH, exceto rotas sem idempotência
                    var requestMethod = handlerMethod.getMethod();
                    boolean isWriteOp = java.util.Arrays.stream(requestMethod.getAnnotations())
                            .anyMatch(a -> a instanceof org.springframework.web.bind.annotation.PostMapping
                                    || a instanceof org.springframework.web.bind.annotation.PatchMapping);

                    if (isWriteOp) {
                        // monta o path completo: prefixo da classe + path do método
                        String classPath = "";
                        var classMapping = org.springframework.core.annotation.AnnotatedElementUtils
                                .findMergedAnnotation(handlerMethod.getBeanType(), org.springframework.web.bind.annotation.RequestMapping.class);
                        if (classMapping != null && classMapping.value().length > 0) classPath = classMapping.value()[0];

                        String methodPath = "";
                        var methodMapping = org.springframework.core.annotation.AnnotatedElementUtils
                                .findMergedAnnotation(requestMethod, org.springframework.web.bind.annotation.RequestMapping.class);
                        if (methodMapping != null && methodMapping.path().length > 0) methodPath = methodMapping.path()[0];

                        String fullPath = classPath + methodPath;

                        // rotas excluídas da exigência de Idempotency-Key
                        boolean excluded = fullPath.equals("/session")
                                || fullPath.startsWith("/idempotency-demo");

                        if (!excluded) {
                            var keyParam = new io.swagger.v3.oas.models.parameters.HeaderParameter()
                                    .name("Idempotency-Key")
                                    .description("Unique UUID to ensure idempotency. Same key + same body = cached response. Same key + different body = 422.")
                                    .required(true)
                                    .schema(new StringSchema().example("550e8400-e29b-41d4-a716-446655440000"));
                            if (operation.getParameters() == null) operation.setParameters(new java.util.ArrayList<>());
                            boolean alreadyPresent = operation.getParameters().stream()
                                    .anyMatch(p -> "Idempotency-Key".equals(p.getName()));
                            if (!alreadyPresent) operation.addParametersItem(keyParam);
                        }
                    }

                    return operation;
                })
                .addOpenApiCustomizer(openApi -> {
                    // personalizar v2 (informações)
                    openApi.getInfo()
                            .title("LIB84 API RESTful v2 Authenticated - Books & Loans Management")
                            .version("2.0.0")
                            .description("""
                                    Welcome to **LIB84 API v2** — the authenticated, role-based evolution of the LIB84 library system.
                                    
                                    ### What's New in V2
                                    - **Native Header Versioning:** Pass `X-API-Version: 2` on all requests.
                                    - **JWT Authentication Required:** Authenticate via `POST /session` and click **Authorize** above with your Bearer token.
                                    - **Context-Aware Resources:** Endpoints like `/loans` and `/profiles` automatically extract the authenticated `user_id` from the JWT payload.
                                    - **Role-Based Access Control (RBAC):** Write operations (`POST`, `PUT`, `DELETE`) on catalog resources require `ADMIN` privileges.
                                    - **Rate Limiting by Plan:** Request quotas enforced per user subscription plan (`BASIC` vs `PREMIUM`).
                                    
                                    **Repository**: https://github.com/Henrry-Maximo/lib84_api
                                    """);
                    add500ToAllOperations(openApi);
                    if (openApi.getPaths() != null) {
                        openApi.getPaths().forEach((path, item) ->
                                item.readOperations().forEach(op -> {
                                    if (op.getSecurity() == null) {
                                        op.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
                                    }
                                })
                        );
                    }
                })
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT token obtained from POST /session (V2 only). Format: Bearer <token>")
                        )
                )
                .info(new Info()
                        .title("LIB84 API RESTful - Books & Loans Management")
                        .version(appVersion)
                        .description("""
                                Welcome to the LIB84 RESTful API, a digital library management server \
                                powered by Spring Boot and compliant with the OpenAPI 3.0 specification. \
                                Designed around REST maturity best practices, this API delivers paginated \
                                collections, strict Bean Validation, and HATEOAS hypermedia links in HAL \
                                format. Feel free to explore the endpoints, test requests directly through \
                                this interactive UI, or contribute to the project's continuous improvement \
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
