package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.CategoryModelAssembler;
import com.example.apirestspringboot.dto.CategoryRecordDto;
import com.example.apirestspringboot.entity.Category;
import com.example.apirestspringboot.service.CategoryService;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories-v2", description = "Category management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class CategoryV2Controller {

    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;
    private final PagedResourcesAssembler<Category> pagedResourcesAssembler;

    public CategoryV2Controller(CategoryService categoryService, CategoryModelAssembler assembler, PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all categories (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned a paginated list of categories"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    @GetMapping(version = "2")
    public ResponseEntity<PagedModel<EntityModel<Category>>> all(
            @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(categoryService.getAll(pageable), assembler));
    }

    @Operation(summary = "Get a category by ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the category with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @GetMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Category>> getById(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(categoryService.getById(id)));
    }

    @Operation(summary = "Search category by title (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid search parameter",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"title\": [\"title must not be blank\"]}"))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the category with title: 'engenharia de software'.\"}")))
    })
    @GetMapping(path = "/search", version = "2")
    public ResponseEntity<EntityModel<Category>> getByTitle(
            @RequestParam(name = "title")
            @NotBlank(message = "title must not be blank")
            @Schema(example = "Engenharia de Software")
            String title) {
        return ResponseEntity.ok(assembler.toModel(categoryService.getByTitle(title)));
    }

    @Operation(summary = "Create a category (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"title\": [\"title is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "409", description = "Category already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"category 'engenharia de software' already exists.\"}")))
    })
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<Category>> create(@RequestBody @Valid CategoryRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(categoryService.create(dto)));
    }

    @Operation(summary = "Update a category (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"title\": [\"title is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the category with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "409", description = "Category already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"category 'engenharia de software' already exists.\"}")))
    })
    @PutMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Category>> update(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid CategoryRecordDto dto) {
        return ResponseEntity.ok(assembler.toModel(categoryService.update(id, dto)));
    }

    @Operation(summary = "Delete a category (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the category with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
