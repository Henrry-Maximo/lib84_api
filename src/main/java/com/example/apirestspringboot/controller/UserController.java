package com.example.apirestspringboot.controller;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management, featuring options such as listing, searching, creating, updating, and deleting.")
public class UserController {
    private final UserService userService;
    private final UserModelAssembler assembler;
    private final PagedResourcesAssembler<User> pagedResourcesAssembler;

    public UserController(
            UserService userService,
            UserModelAssembler assembler,
            PagedResourcesAssembler<User> pagedResourcesAssembler) {
        this.userService = userService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of all users")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<User>>> all(@ParameterObject @PageableDefault(size = 10, sort = "email") Pageable pageable) {
        Page<User> users = this.userService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(users, assembler));
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> getById(
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID id
    ) {
        return ResponseEntity.ok(assembler.toModel(userService.getById(id)));
    }

    @Operation(summary = "Search user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameter",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid email",
                                    value = """
                                            {
                                                "email": [
                                                    "email must not be blank"
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the user with email: 'johndoe@example.com'."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/search")
    public ResponseEntity<EntityModel<User>> getByEmail(
            @RequestParam(name = "email")
            @NotBlank(message = "email must not be blank")
            @Email(message = "email must be a valid address")
            @Schema(example = "johndoe@example.com")
            String email
    ) {
        return ResponseEntity.ok(assembler.toModel(userService.getByEmail(email)));
    }

    @Operation(summary = "Create a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            summary = "Request body contains invalid field values",
                                            value = """
                                                    {
                                                        "email": [
                                                            "email is required"
                                                        ],
                                                        "password": [
                                                            "password is required"
                                                        ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Malformed JSON",
                                            summary = "Request body contains invalid JSON syntax",
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
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "email 'johndoe@example.com' already exists."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<User>> create(@RequestBody @Valid UserRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(userService.create(dto)));
    }

    @Operation(summary = "Update all user fields")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            summary = "Request body contains invalid field values",
                                            value = """
                                                    {
                                                        "email": [
                                                            "email is required"
                                                        ],
                                                        "password": [
                                                            "password is required"
                                                        ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Malformed JSON",
                                            summary = "Request body contains invalid JSON syntax",
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
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                            }
                                            """
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<User>> update(
            @RequestBody @Valid UserRecordDto dto,
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID id
    ) {
        return ResponseEntity.ok(assembler.toModel(userService.update(id, dto)));
    }

    @Operation(summary = "Partially update a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<User>> updateSameFields(
            @RequestBody @Valid UserPatchDto dto,
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID id
    ) {
        return ResponseEntity.ok(assembler.toModel(userService.updateSameFields(id, dto)));
    }

    @Operation(summary = "Delete a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID id
    ) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
