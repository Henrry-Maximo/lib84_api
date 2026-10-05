package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.LoanModelAssembler;
import com.example.apirestspringboot.dto.LoanRecordDto;
import com.example.apirestspringboot.entity.Loan;
import com.example.apirestspringboot.service.LoanService;
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
@RequestMapping("/loans")
@Tag(name = "Loans", description = "Loan management, featuring options such as listing, searching, creating, updating, and deleting.")
public class LoanController {

    private final LoanService loanService;
    private final LoanModelAssembler assembler;
    private final PagedResourcesAssembler<Loan> pagedResourcesAssembler;

    public LoanController(LoanService loanService, LoanModelAssembler assembler, PagedResourcesAssembler<Loan> pagedResourcesAssembler) {
        this.loanService = loanService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all loans")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Loan>>> all(@ParameterObject @PageableDefault(size = 10, sort = "loanDate") Pageable pageable) {
        Page<Loan> loans = this.loanService.getAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(loans, assembler));
    }

    @Operation(summary = "Get a loan by id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Loan>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(loanService.getById(id)));
    }

    @Operation(summary = "Get loans by user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<Loan>>> getByUser(@PathVariable UUID userId) {
        CollectionModel<EntityModel<Loan>> loans = CollectionModel.of(loanService.getByUser(userId).stream().map(assembler::toModel).toList());
        return ResponseEntity.ok(loans);
    }

    @Operation(summary = "Create a loan")
    @PostMapping
    public ResponseEntity<EntityModel<Loan>> create(@RequestBody @Valid LoanRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(loanService.create(dto)));
    }

    @Operation(summary = "Update a loan")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Loan>> update(@RequestBody @Valid LoanRecordDto dto, @PathVariable UUID id) {
        Loan loan = loanService.getById(id);
        return ResponseEntity.ok(assembler.toModel(loanService.update(loan, dto)));
    }

    @Operation(summary = "Delete a loan")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        loanService.delete(loanService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
