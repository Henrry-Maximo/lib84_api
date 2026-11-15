package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.assembler.ProfileModelAssembler;
import com.example.apirestspringboot.dto.ProfileV2RecordDto;
import com.example.apirestspringboot.entity.Profile;
import com.example.apirestspringboot.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "Profile for registar informations about user.")
public class ProfileV2Controller {

    private final ProfileService profileService;
    private final ProfileModelAssembler assembler;

    public ProfileV2Controller(ProfileService profileService, ProfileModelAssembler assembler) {
        this.profileService = profileService;
        this.assembler = assembler;
    }

    @Operation(summary = "Create a profile for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = """
                                                    {
                                                        "name": ["name is required"]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Malformed JSON",
                                            value = """
                                                    {
                                                        "error": "invalid JSON request body. Check the JSON syntax and field types."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")
                    )
            )
    })
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<Profile>> create(
            @RequestBody @Valid ProfileV2RecordDto dto,
            HttpServletRequest request
    ) {
        UUID userId = UUID.fromString((String) request.getAttribute("user_id"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(profileService.create(dto, userId)));
    }
}
