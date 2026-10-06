package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.SupplierModelAssembler;
import com.example.apirestspringboot.dto.SupplierRecordDto;
import com.example.apirestspringboot.entity.Supplier;
import com.example.apirestspringboot.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/suppliers")
@Tag(name = "Suppliers", description = "Supplier management, featuring options such as listing, searching, creating, updating, and deleting.")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierModelAssembler assembler;
    private final PagedResourcesAssembler<Supplier> pagedResourcesAssembler;

    public SupplierController(
            SupplierService supplierService,
            SupplierModelAssembler assembler,
            PagedResourcesAssembler<Supplier> pagedResourcesAssembler) {
        this.supplierService = supplierService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all suppliers")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of suppliers")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Supplier>>> all(@ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Supplier> suppliers = this.supplierService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(suppliers, assembler));
    }

    @Operation(summary = "Get a supplier by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Supplier found"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Supplier>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(supplierService.getById(id)));
    }

    @Operation(summary = "Search supplier by name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Supplier found"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping("/search")
    public ResponseEntity<EntityModel<Supplier>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(assembler.toModel(supplierService.getByName(name)));
    }

    @Operation(summary = "Create a supplier")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Supplier created"),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Supplier>> create(@RequestBody @Valid SupplierRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(supplierService.create(dto)));
    }

    @Operation(summary = "Update a supplier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Supplier updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Supplier>> update(@RequestBody @Valid SupplierRecordDto dto, @PathVariable UUID id) {
        Supplier supplier = supplierService.getById(id);

        return ResponseEntity.ok(assembler.toModel(supplierService.update(supplier, dto)));
    }

    @Operation(summary = "Delete a supplier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Supplier deleted"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        supplierService.delete(supplierService.getById(id));

        return ResponseEntity.noContent().build();
    }
}
