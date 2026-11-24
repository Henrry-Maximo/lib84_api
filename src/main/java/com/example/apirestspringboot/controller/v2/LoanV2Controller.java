package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.LoanModelAssembler;
import com.example.apirestspringboot.dto.v2.LoanV2RecordDto;
import com.example.apirestspringboot.entity.Loan;
import com.example.apirestspringboot.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loans-v2", description = "Loan management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class LoanV2Controller {

    private final LoanService loanService;
    private final LoanModelAssembler assembler;
    private final PagedResourcesAssembler<Loan> pagedResourcesAssembler;

    public LoanV2Controller(LoanService loanService, LoanModelAssembler assembler, PagedResourcesAssembler<Loan> pagedResourcesAssembler) {
        this.loanService = loanService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all loans (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned a paginated list of loans"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @GetMapping(version = "2")
    public ResponseEntity<PagedModel<EntityModel<Loan>>> all(
            @ParameterObject @PageableDefault(size = 10, sort = "loanDate") Pageable pageable) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(loanService.getAll(pageable), assembler));
    }

    @Operation(summary = "Get a loan by ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "404", description = "Loan not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the loan with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @GetMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Loan>> getById(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(loanService.getById(id)));
    }

    @Operation(summary = "Get my loans (v2)", description = "Returns all loans belonging to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned loans for the authenticated user"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @GetMapping(path = "/me", version = "2")
    public ResponseEntity<CollectionModel<EntityModel<Loan>>> getMyLoans(HttpServletRequest request) {
        UUID userId = UUID.fromString((String) request.getAttribute("user_id"));
        return ResponseEntity.ok(CollectionModel.of(
                loanService.getByUser(userId).stream().map(assembler::toModel).toList()));
    }

    @Operation(summary = "Create a loan for the authenticated user (v2)",
            description = "The loan is automatically associated with the authenticated user. No userId needed in the body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"bookIds\": [\"bookIds must contain at least one book\"], \"loanDateScheduled\": [\"loanDateScheduled is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Books not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"one or more books not found.\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<Loan>> create(
            @RequestBody @Valid LoanV2RecordDto dto,
            HttpServletRequest request) {
        UUID userId = UUID.fromString((String) request.getAttribute("user_id"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(loanService.createForUser(userId, dto.bookIds(), dto.loanDateScheduled())));
    }

    @Operation(summary = "Delete a loan (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Loan deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"authentication required.\"}"))),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"access denied: admin role required.\"}"))),
            @ApiResponse(responseCode = "404", description = "Loan not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the loan with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded for your plan", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"too many requests. Rate limit exceeded for your plan.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        loanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
