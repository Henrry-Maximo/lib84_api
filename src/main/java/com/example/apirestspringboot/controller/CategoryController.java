package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.CategoryModelAssembler;
import com.example.apirestspringboot.dto.CategoryRecordDto;
import com.example.apirestspringboot.entity.Category;
import com.example.apirestspringboot.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
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

    public CategoryController(CategoryService categoryService, CategoryModelAssembler assembler, PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Category>>> all(@ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Category> categories = this.categoryService.getAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(categories, assembler));
    }

    @Operation(summary = "Get a category by id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Category>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(categoryService.getById(id)));
    }

    @Operation(summary = "Create a category")
    @PostMapping
    public ResponseEntity<EntityModel<Category>> create(@RequestBody @Valid CategoryRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(categoryService.create(dto)));
    }

    @Operation(summary = "Update a category")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Category>> update(@RequestBody @Valid CategoryRecordDto dto, @PathVariable UUID id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(assembler.toModel(categoryService.update(category, dto)));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        categoryService.delete(categoryService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
