package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.dto.v2.AuthUsersV2RecordDto;
import com.example.apirestspringboot.service.AuthenticateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/session")
@Tag(name = "Session-v2", description = "Authentication endpoint. Returns a JWT token on valid credentials - version 2 (send API-Version: 2).")
public class SessionV2Controller {

    private final AuthenticateService authenticateService;

    public SessionV2Controller(AuthenticateService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @SecurityRequirements({})
    @Operation(summary = "Authenticate and obtain a JWT token (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"email\": [\"email is required\"], \"password\": [\"password is required\"]}"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"credentials invalid.\"}")))
    })
    @PostMapping(version = "2")
    public ResponseEntity<Map<String, String>> session(@RequestBody @Valid AuthUsersV2RecordDto dto) {
        var token = this.authenticateService.execute(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
