package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.ProfileModelAssembler;
import com.example.apirestspringboot.dto.v2.ProfileV2RecordDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * V2 of the profiles API. Same URLs as V1, chosen with "X-API-Version: 2".
 * GET /profiles, GET /profiles/{id} and GET /profiles/user/{userId} did not change in contract,
 * so they keep version = "1" and serve every version.
 */
@RestController
@RequestMapping("/profiles")
@Tag(name = "Profiles-v2", description = "Profile management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class ProfileV2Controller {

    private final ProfileService profileService;
    private final ProfileModelAssembler assembler;

    public ProfileV2Controller(ProfileService profileService, ProfileModelAssembler assembler) {
        this.profileService = profileService;
        this.assembler = assembler;
    }

    @Operation(summary = "Get the profile of the authenticated user (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found for the authenticated user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the profile with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")
                    )
            )
    })
    @GetMapping(path = "/me", version = "2")
    public ResponseEntity<EntityModel<Profile>> getMyProfile(HttpServletRequest request) {
        UUID userId = UUID.fromString((String) request.getAttribute("user_id"));

        return ResponseEntity.ok(assembler.toModel(profileService.getByUserId(userId)));
    }

    @Operation(summary = "Create a profile for the authenticated user (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"name\": [\"name is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
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

    @Operation(summary = "Update the authenticated user's profile (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"name\": [\"name is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user does not own this profile",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"you are not allowed to update another user's profile.\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the profile with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")
                    )
            )
    })
    @PutMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Profile>> update(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid ProfileV2RecordDto dto,
            HttpServletRequest request
    ) {
        UUID userId = UUID.fromString((String) request.getAttribute("user_id"));

        return ResponseEntity.ok(assembler.toModel(profileService.update(id, dto, userId)));
    }

    @Operation(summary = "Delete the authenticated user's profile (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Authenticated user does not own this profile",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"you are not allowed to delete another user's profile.\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the profile with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")
                    )
            )
    })
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            HttpServletRequest request
    ) {
        UUID userId = UUID.fromString((String) request.getAttribute("user_id"));

        profileService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
