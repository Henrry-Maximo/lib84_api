package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.BookModelAssembler;
import com.example.apirestspringboot.dto.BookRecordDto;
import com.example.apirestspringboot.entity.Book;
import com.example.apirestspringboot.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management, featuring options such as listing, searching, creating, updating, and deleting.")
public class BookController {

    private final BookService bookService;
    private final BookModelAssembler assembler;
    private final PagedResourcesAssembler<Book> pagedResourcesAssembler;

    public BookController(BookService bookService, BookModelAssembler assembler, PagedResourcesAssembler<Book> pagedResourcesAssembler) {
        this.bookService = bookService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all books")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Book>>> all(@ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Book> books = this.bookService.getAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(books, assembler));
    }

    @Operation(summary = "Get a book by id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Book>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(bookService.getById(id)));
    }

    @Operation(summary = "Get books by category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CollectionModel<EntityModel<Book>>> getByCategory(@PathVariable UUID categoryId) {
        CollectionModel<EntityModel<Book>> books = CollectionModel.of(bookService.getByCategory(categoryId).stream().map(assembler::toModel).toList());
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Create a book")
    @PostMapping
    public ResponseEntity<EntityModel<Book>> create(@RequestBody @Valid BookRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(bookService.create(dto)));
    }

    @Operation(summary = "Update a book")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Book>> update(@RequestBody @Valid BookRecordDto dto, @PathVariable UUID id) {
        Book book = bookService.getById(id);
        return ResponseEntity.ok(assembler.toModel(bookService.update(book, dto)));
    }

    @Operation(summary = "Delete a book")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        bookService.delete(bookService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
