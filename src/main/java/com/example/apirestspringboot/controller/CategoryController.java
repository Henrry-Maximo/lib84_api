package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.CategoryModelAssembler;
import com.example.apirestspringboot.dto.CategoryRecordDto;
import com.example.apirestspringboot.entity.Category;
import com.example.apirestspringboot.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Category management, featuring options such as listing, searching, creating, updating, and deleting.")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;
    private final PagedResourcesAssembler<Category> pagedResourcesAssembler;

    public CategoryController(
            CategoryService categoryService,
            CategoryModelAssembler assembler,
            PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of categories")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Category>>> all(@ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Category> categories = this.categoryService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(categories, assembler));
    }

    @Operation(summary = "Get a category by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Could not find the category with ID: ...\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Category>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(categoryService.getById(id)));
    }

    @Operation(summary = "Search category by title")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Could not find the category with ID: ...\"}")))
    })
    @GetMapping("/search")
    public ResponseEntity<EntityModel<Category>> getByTitle(@RequestParam String title) {
        return ResponseEntity.ok(assembler.toModel(categoryService.getByTitle(title)));
    }

    @Operation(summary = "Create a category")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created"),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"title\": \"must not be blank\"}")))
    })
    @PostMapping
    public ResponseEntity<EntityModel<Category>> create(@RequestBody @Valid CategoryRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(categoryService.create(dto)));
    }

    @Operation(summary = "Update a category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"title\": \"must not be blank\"}"))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Could not find the category with ID: ...\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Category>> update(@RequestBody @Valid CategoryRecordDto dto, @PathVariable UUID id) {
        Category category = categoryService.getById(id);

        return ResponseEntity.ok(assembler.toModel(categoryService.update(category, dto)));
    }

    @Operation(summary = "Delete a category")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted"),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Could not find the category with ID: ...\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        categoryService.delete(categoryService.getById(id));

        return ResponseEntity.noContent().build();
    }
}
