package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.AuthorModelAssembler;
import com.example.apirestspringboot.dto.AuthorRecordDto;
import com.example.apirestspringboot.entity.Author;
import com.example.apirestspringboot.service.AuthorService;
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
@RequestMapping("/authors")
@Tag(name = "Authors", description = "Author management, featuring options such as listing, searching, creating, updating, and deleting.")
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorModelAssembler assembler;
    private final PagedResourcesAssembler<Author> pagedResourcesAssembler;

    public AuthorController(AuthorService authorService, AuthorModelAssembler assembler, PagedResourcesAssembler<Author> pagedResourcesAssembler) {
        this.authorService = authorService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all authors")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Author>>> all(@ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Author> authors = this.authorService.getAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(authors, assembler));
    }

    @Operation(summary = "Get an author by id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Author>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(authorService.getById(id)));
    }

    @Operation(summary = "Search author by name")
    @GetMapping("/search")
    public ResponseEntity<EntityModel<Author>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(assembler.toModel(authorService.getByName(name)));
    }

    @Operation(summary = "Create an author")
    @PostMapping
    public ResponseEntity<EntityModel<Author>> create(@RequestBody @Valid AuthorRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(authorService.create(dto)));
    }

    @Operation(summary = "Update an author")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Author>> update(@RequestBody @Valid AuthorRecordDto dto, @PathVariable UUID id) {
        Author author = authorService.getById(id);
        return ResponseEntity.ok(assembler.toModel(authorService.update(author, dto)));
    }

    @Operation(summary = "Delete an author")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        authorService.delete(authorService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
