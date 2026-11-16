package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.BookModelAssembler;
import com.example.apirestspringboot.dto.BookRecordDto;
import com.example.apirestspringboot.entity.Book;
import com.example.apirestspringboot.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Books-v2", description = "Book management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class BookV2Controller {

    private final BookService bookService;
    private final BookModelAssembler assembler;
    private final PagedResourcesAssembler<Book> pagedResourcesAssembler;

    public BookV2Controller(BookService bookService, BookModelAssembler assembler, PagedResourcesAssembler<Book> pagedResourcesAssembler) {
        this.bookService = bookService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all books (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned a paginated list of books"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    @GetMapping(version = "2")
    public ResponseEntity<PagedModel<EntityModel<Book>>> all(
            @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(bookService.getAll(pageable), assembler));
    }

    @Operation(summary = "Get a book by ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the book with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @GetMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Book>> getById(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(bookService.getById(id)));
    }

    @Operation(summary = "Get books by category ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned books for the given category"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the category with ID: '3fa85f64-5717-4562-b3fc-2c963f66afa6'.\"}")))
    })
    @GetMapping(path = "/category/{categoryId}", version = "2")
    public ResponseEntity<CollectionModel<EntityModel<Book>>> getByCategory(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID categoryId) {
        return ResponseEntity.ok(CollectionModel.of(
                bookService.getByCategory(categoryId).stream().map(assembler::toModel).toList()));
    }

    @Operation(summary = "Create a book (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"title\": [\"must not be blank\"], \"isbn\": [\"must not be blank\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Category, supplier or authors not found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Category not found", value = "{\"error\": \"could not find the category with ID: '3fa85f64-5717-4562-b3fc-2c963f66afa6'.\"}"),
                                    @ExampleObject(name = "Supplier not found", value = "{\"error\": \"could not find the supplier with ID: '3fa85f64-5717-4562-b3fc-2c963f66afa6'.\"}"),
                                    @ExampleObject(name = "Authors not found", value = "{\"error\": \"could not find the authors with ID: ['3fa85f64-5717-4562-b3fc-2c963f66afa6'].\"}")
                            }))
    })
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<Book>> create(@RequestBody @Valid BookRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(bookService.create(dto)));
    }

    @Operation(summary = "Update a book (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"title\": [\"must not be blank\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the book with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @PutMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Book>> update(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid BookRecordDto dto) {
        return ResponseEntity.ok(assembler.toModel(bookService.update(bookService.getById(id), dto)));
    }

    @Operation(summary = "Delete a book (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the book with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
