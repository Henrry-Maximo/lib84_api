package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.AuthorModelAssembler;
import com.example.apirestspringboot.dto.AuthorRecordDto;
import com.example.apirestspringboot.entity.Author;
import com.example.apirestspringboot.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/authors")
@Tag(name = "Authors-v2", description = "Author management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class AuthorV2Controller {

    private final AuthorService authorService;
    private final AuthorModelAssembler assembler;
    private final PagedResourcesAssembler<Author> pagedResourcesAssembler;

    public AuthorV2Controller(
            AuthorService authorService,
            AuthorModelAssembler assembler,
            PagedResourcesAssembler<Author> pagedResourcesAssembler) {
        this.authorService = authorService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all authors (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned a paginated list of authors"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    @GetMapping(version = "2")
    public ResponseEntity<PagedModel<EntityModel<Author>>> all(
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(this.authorService.getAll(pageable), assembler));
    }

    @Operation(summary = "Get an author by ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the author with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @GetMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Author>> getById(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(this.authorService.getById(id)));
    }

    @Operation(summary = "Search author by name (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid search parameter",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"name\": [\"name must not be blank\"]}"))),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the author with name: 'george orwell'.\"}")))
    })
    @GetMapping(path = "/search", version = "2")
    public ResponseEntity<EntityModel<Author>> getByName(
            @RequestParam(name = "name")
            @NotBlank(message = "name must not be blank")
            @Schema(example = "George Orwell")
            String name) {
        return ResponseEntity.ok(assembler.toModel(this.authorService.getByName(name)));
    }

    @Operation(summary = "Create an author (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Admin role required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"name\": [\"must not be blank\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "409", description = "Author already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"author 'george orwell' already exists.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<Author>> create(@RequestBody @Valid AuthorRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(this.authorService.create(dto)));
    }

    @Operation(summary = "Update an author (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author updated"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Admin role required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"name\": [\"must not be blank\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the author with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "409", description = "Author already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"author 'george orwell' already exists.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Author>> update(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid AuthorRecordDto dto) {
        return ResponseEntity.ok(assembler.toModel(this.authorService.update(id, dto)));
    }

    @Operation(summary = "Delete an author (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Author deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the author with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "409", description = "Author is associated with one or more books",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"author with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6 cannot be deleted because it is associated with one or more books.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        this.authorService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
