package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.assembler.LoanModelAssembler;
import com.example.apirestspringboot.dto.LoanRecordDto;
import com.example.apirestspringboot.entity.Loan;
import com.example.apirestspringboot.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loans", description = "Loan management, featuring options such as listing, searching, creating, updating, and deleting.")
public class LoanController {
    private final LoanService loanService;
    private final LoanModelAssembler assembler;
    private final PagedResourcesAssembler<Loan> pagedResourcesAssembler;

    public LoanController(
            LoanService loanService,
            LoanModelAssembler assembler,
            PagedResourcesAssembler<Loan> pagedResourcesAssembler) {
        this.loanService = loanService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all loans")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of loans")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Loan>>> all(@ParameterObject @PageableDefault(size = 10, sort = "loanDate") Pageable pageable) {
        Page<Loan> loans = this.loanService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(loans, assembler));
    }

    @Operation(summary = "Get a loan by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the loan with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Loan>> getById(
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID id
    ) {
        return ResponseEntity.ok(assembler.toModel(loanService.getById(id)));
    }

    @Operation(summary = "Get loans by user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned loans for the given user"),
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<Loan>>> getByUser(
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID userId
    ) {
        CollectionModel<EntityModel<Loan>> loans = CollectionModel.of(
                loanService.getByUser(userId).stream().map(assembler::toModel).toList()
        );

        return ResponseEntity.ok(loans);
    }

    @Operation(summary = "Create a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan created"),
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
                                                        "userId": [
                                                            "userId is required"
                                                        ],
                                                        "bookIds": [
                                                            "bookIds must contain at least one book"
                                                        ],
                                                        "loanDateScheduled": [
                                                            "loanDateScheduled is required"
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
                    description = "User or books not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "Request body userId not found",
                                            value = """
                                                    {
                                                        "error": "could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Books not found",
                                            summary = "One or more bookIds not found",
                                            value = """
                                                    {
                                                        "error": "one or more books not found."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Loan>> create(@RequestBody @Valid LoanRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(loanService.create(dto)));
    }

    @Operation(summary = "Update a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan updated"),
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
                                                        "userId": [
                                                            "userId is required"
                                                        ],
                                                        "bookIds": [
                                                            "bookIds must contain at least one book"
                                                        ],
                                                        "loanDateScheduled": [
                                                            "loanDateScheduled is required"
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
                    description = "Loan, user or books not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Loan not found",
                                            summary = "Path ID not found",
                                            value = """
                                                    {
                                                        "error": "could not find the loan with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "Request body userId not found",
                                            value = """
                                                    {
                                                        "error": "could not find the user with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Books not found",
                                            summary = "One or more bookIds not found",
                                            value = """
                                                    {
                                                        "error": "one or more books not found."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Loan>> update(
            @RequestBody @Valid LoanRecordDto dto,
            @PathVariable
            @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            UUID id
    ) {
        return ResponseEntity.ok(assembler.toModel(loanService.update(id, dto)));
    }

    @Operation(summary = "Delete a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Loan deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "error": "could not find the loan with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
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
        loanService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
