package com.example.apirestspringboot.controller.v2;

import com.example.apirestspringboot.assembler.SupplierModelAssembler;
import com.example.apirestspringboot.dto.SupplierRecordDto;
import com.example.apirestspringboot.entity.Supplier;
import com.example.apirestspringboot.service.SupplierService;
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
@Tag(name = "Suppliers-v2", description = "Supplier management, featuring options such as listing, searching, creating, updating, and deleting - version 2 (send X-API-Version: 2).")
public class SupplierV2Controller {

    private final SupplierService supplierService;
    private final SupplierModelAssembler assembler;
    private final PagedResourcesAssembler<Supplier> pagedResourcesAssembler;

    public SupplierV2Controller(SupplierService supplierService, SupplierModelAssembler assembler, PagedResourcesAssembler<Supplier> pagedResourcesAssembler) {
        this.supplierService = supplierService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all suppliers (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned a paginated list of suppliers"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    @GetMapping(version = "2")
    public ResponseEntity<PagedModel<EntityModel<Supplier>>> all(
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(supplierService.getAll(pageable), assembler));
    }

    @Operation(summary = "Get a supplier by ID (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the supplier with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @GetMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Supplier>> getById(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        return ResponseEntity.ok(assembler.toModel(supplierService.getById(id)));
    }

    @Operation(summary = "Search supplier by name (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid search parameter",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"name\": [\"name must not be blank\"]}"))),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the supplier with name: 'editora exemplo ltda'.\"}")))
    })
    @GetMapping(path = "/search", version = "2")
    public ResponseEntity<EntityModel<Supplier>> getByName(
            @RequestParam(name = "name")
            @NotBlank(message = "name must not be blank")
            @Schema(example = "Editora Exemplo Ltda")
            String name) {
        return ResponseEntity.ok(assembler.toModel(supplierService.getByName(name)));
    }

    @Operation(summary = "Create a supplier (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"name\": [\"name is required\"], \"email\": [\"email is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            }))
    })
    @PostMapping(version = "2")
    public ResponseEntity<EntityModel<Supplier>> create(@RequestBody @Valid SupplierRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(supplierService.create(dto)));
    }

    @Operation(summary = "Update a supplier (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "{\"name\": [\"name is required\"], \"email\": [\"email is required\"]}"),
                                    @ExampleObject(name = "Malformed JSON", value = "{\"error\": \"invalid JSON request body. Check the JSON syntax and field types.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the supplier with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @PutMapping(path = "/{id}", version = "2")
    public ResponseEntity<EntityModel<Supplier>> update(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
            @RequestBody @Valid SupplierRecordDto dto) {
        return ResponseEntity.ok(assembler.toModel(supplierService.update(id, dto)));
    }

    @Operation(summary = "Delete a supplier (v2)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"could not find the supplier with ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6\"}")))
    })
    @DeleteMapping(path = "/{id}", version = "2")
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
