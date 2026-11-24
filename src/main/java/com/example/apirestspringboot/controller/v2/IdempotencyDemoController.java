package com.example.apirestspringboot.controller.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/*
 * GET /idempotency-demo retorna documentação em JSON + tabelas. POST /idempotency-demo/echo é o endpoint de teste: manda
 * qualquer body com Idempotency-Key, repete a requisição e vê o Idempotent-Replayed: true.
 */

@RestController
@RequestMapping("/idempotency-demo")
@Tag(name = "Idempotency-v2", description = "Demonstrates idempotency behavior for POST and PATCH operations in V2.")
public class IdempotencyDemoController {

    @SecurityRequirements({})
    @Operation(
            summary = "How idempotency works (v2)",
            description = """
                    ## Idempotency in LIB84 API v2
                    
                    All **POST** and **PATCH** requests in V2 require the `Idempotency-Key` header.
                    
                    ### Rules
                    
                    | Scenario | Behavior |
                    |---|---|
                    | First request with a key | Processed normally, response stored for 24h |
                    | Same key + same body | `200` with cached response + `Idempotent-Replayed: true` header |
                    | Same key + different body | `422 Unprocessable Entity` |
                    | Concurrent request with same key | `409 Conflict` |
                    | Key expired (after 24h) | Treated as a new request |
                    
                    ### How to test
                    
                    1. Generate a UUID key: e.g. `550e8400-e29b-41d4-a716-446655440000`
                    2. Send `POST /idempotency-demo/echo` with `Idempotency-Key: <your-uuid>` and any JSON body
                    3. Repeat the same request — you will receive the cached response with `Idempotent-Replayed: true`
                    4. Change the body keeping the same key — you will receive `422`
                   
                    """
    )
    @ApiResponse(responseCode = "200", description = "Idempotency documentation")
    @GetMapping(version = "2")
    public ResponseEntity<Map<String, Object>> docs() {
        return ResponseEntity.ok(Map.of(
                "feature", "Idempotency",
                "header", "Idempotency-Key",
                "ttl", "24 hours",
                "applies_to", new String[]{"POST", "PATCH"},
                "version", "V2 only"
        ));
    }

//    @SecurityRequirements({})
//    @Operation(
//            summary = "Echo endpoint to test idempotency (v2)",
//            description = "Send any JSON body with an `Idempotency-Key` header. Repeat the request to see the cached response replayed."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Request processed or replayed from cache",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(example = "{\"echo\": {\"message\": \"hello\"}, \"processedAt\": \"2025-01-01T00:00:00Z\"}"))),
//            @ApiResponse(responseCode = "409", description = "Concurrent request with same key in progress",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(example = "{\"error\": \"A request with this Idempotency-Key is already being processed.\"}"))),
//            @ApiResponse(responseCode = "422", description = "Missing key or key reused with different body",
//                    content = @Content(mediaType = "application/json",
//                            examples = {
//                                    @ExampleObject(name = "Missing key", value = "{\"error\": \"Idempotency-Key header is required for POST and PATCH requests.\"}"),
//                                    @ExampleObject(name = "Body mismatch", value = "{\"error\": \"Idempotency-Key reused with a different request body.\"}")
//                            }))
//    })
//    @PostMapping(path = "/echo", version = "2")
//    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> body) {
//        return ResponseEntity.ok(Map.of(
//                "echo", body,
//                "processedAt", Instant.now().toString(),
//                "requestId", UUID.randomUUID().toString()
//        ));
//    }
}

// ### Idempotency by HTTP method
//
//                    | Method | Idempotent by nature? | Requires key? |
//        |---|---|---|
//        | GET | ✅ Yes | No |
//        | PUT | ✅ Yes | No |
//        | DELETE | ✅ Yes | No |
//        | POST | ❌ No | **Yes** |
//        | PATCH | ❌ No | **Yes** |
