package com.example.apirestspringboot.controller;

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
import jakarta.validation.constraints.NotBlank;
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

    public BookController(
            BookService bookService,
            BookModelAssembler assembler,
            PagedResourcesAssembler<Book> pagedResourcesAssembler) {
        this.bookService = bookService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all books")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of books")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Book>>> all(@ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Book> books = this.bookService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(books, assembler));
    }

    @Operation(summary = "Get an book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"could not find the book with ID: ...\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Book>> getById(@PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(this.bookService.getById(id)));
    }

    @Operation(summary = "Search book by ID category")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Returned books for the given category"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the category with ID: '3fa85f64-5717-4562-b3fc-2c963f66afa6'."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CollectionModel<EntityModel<Book>>> getByCategory(@PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID categoryId) {
        CollectionModel<EntityModel<Book>> books = CollectionModel.of(this.bookService.getByCategory(categoryId).stream().map(assembler::toModel).toList());

        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Create a book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"title\": [\"must not be blank\"], \"isbn\": [\"must not be blank\"], \"amount\": [\"must not be null\"], \"datePublication\": [\"must not be null\"], \"categoryId\": [\"must not be null\"], \"supplierId\": [\"must not be null\"], \"authorIds\": [\"must not be empty\"]}"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Category not found",
                                            summary = "Request body category id not found",
                                            value = """
                                                    {
                                                        "error": "could not find the category with ID: '3fa85f64-5717-4562-b3fc-2c963f66afa6'."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Supplier not found",
                                            summary = "Request body supplier id not found",
                                            value = """
                                                    {
                                                        "error": "could not find the supplier with ID: '3fa85f64-5717-4562-b3fc-2c963f66afa6'."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Authors not found",
                                            summary = "Request body authors id not found",
                                            value = """
                                                    {
                                                        "error": "could not find the authors with ID: ['3fa85f64-5717-4562-b3fc-2c963f66afa6']."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Book>> create(@RequestBody @Valid BookRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(this.bookService.create(dto)));
    }

    @Operation(summary = "Update an book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"title\": [\"title is required\"], \"isbn\": [\"isbn is required\"], \"amount\": [\"amount is required\"], \"datePublication\": [\"datePublication is required\"], \"categoryId\": [\"categoryId is required\"], \"supplierId\": [\"supplierId is required\"], \"authorIds\": [\"authorId/authorsId is required\"]}"))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"could not find the book with ID: ...\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Book>> update(@RequestBody @Valid BookRecordDto dto, @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        Book book = bookService.getById(id);

        return ResponseEntity.ok(assembler.toModel(bookService.update(book, dto)));
    }

    @Operation(summary = "Delete a book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the book with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
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
        bookService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
