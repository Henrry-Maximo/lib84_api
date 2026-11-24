package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.UserModelAssembler;
import com.example.apirestspringboot.dto.UserPatchDto;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users-v2", description = "User management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class UserV2Controller {

    private final UserService userService;
    private final UserModelAssembler assembler;
    private final PagedResourcesAssembler<User> pagedResourcesAssembler;

    public UserV2Controller(UserService userService, UserModelAssembler assembler, PagedResourcesAssembler<User> pagedResourcesAssembler) {
        this.userService = userService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all users (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned a paginated list of all users"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"access denied: admin role required.\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(version = "2")
    public ResponseEntity<PagedModel<EntityModel<User>>> all(
            @ParameterObject @PageableDefault(size = 10, sort = "email") Pageable pageable) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(userService.getAll(pageable), assembler));
    }

    @Operation(summary = "Get a user by ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"access denied: admin role required.\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<User>> getById(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(userService.getById(id)));
    }

    @Operation(summary = "Search user by email (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"access denied: admin role required.\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameter",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"email\": [\"email must not be blank\"]}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the user with email: 'johndoe@example.com'.\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/search", version = "2")
    public ResponseEntity<EntityModel<User>> getByEmail(
            @RequestParam(name = "email")
            @NotBlank(message = "email must not be blank")
            @Email(message = "email must be a valid address")
            @Schema(example = "johndoe@example.com")
            String email) {
        return ResponseEntity.ok(assembler.toModel(userService.getByEmail(email)));
    }

    @SecurityRequirements({})
    @Operation(summary = "Create a user (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"email\": [\"email is required\"], \"password\": [\"password is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"email 'johndoe@example.com' already exists.\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<User>> create(@RequestBody @Valid UserRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(userService.create(dto)));
    }

    @Operation(summary = "Update all user fields (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"email\": [\"email is required\"], \"password\": [\"password is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PutMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<User>> update(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid UserRecordDto dto) {
        return ResponseEntity.ok(assembler.toModel(userService.update(id, dto)));
    }

    @Operation(summary = "Partially update a user (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PatchMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<User>> updateSameFields(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid UserPatchDto dto) {
        return ResponseEntity.ok(assembler.toModel(userService.updateSameFields(id, dto)));
    }

    @Operation(summary = "Delete a user (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
