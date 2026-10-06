package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.ProfileModelAssembler;
import com.example.apirestspringboot.dto.ProfileRecordDto;
import com.example.apirestspringboot.entity.Profile;
import com.example.apirestspringboot.service.ProfileService;
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
@RequestMapping("/profiles")
@Tag(name = "Profile", description = "Profile management, featuring options such as listing, searching, creating, updating, and deleting.")
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileModelAssembler assembler;
    private final PagedResourcesAssembler<Profile> pagedResourcesAssembler;

    public ProfileController(
            ProfileService profileService,
            ProfileModelAssembler assembler,
            PagedResourcesAssembler<Profile> pagedResourcesAssembler) {
        this.profileService = profileService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all profiles")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of profiles")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Profile>>> all(@ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Profile> profiles = this.profileService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(profiles, assembler));
    }

    @Operation(summary = "Get a profile by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile found"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Profile>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(profileService.getById(id)));
    }

    @Operation(summary = "Get profile by user id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile found"),
        @ApiResponse(responseCode = "404", description = "Profile not found for user")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<EntityModel<Profile>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(assembler.toModel(profileService.getByUserId(userId)));
    }

    @Operation(summary = "Create a profile")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Profile created"),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Profile>> create(@RequestBody @Valid ProfileRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(profileService.create(dto)));
    }

    @Operation(summary = "Update a profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Profile>> update(@RequestBody @Valid ProfileRecordDto dto, @PathVariable UUID id) {
        Profile profile = profileService.getById(id);

        return ResponseEntity.ok(assembler.toModel(profileService.update(profile, dto)));
    }

    @Operation(summary = "Delete a profile")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Profile deleted"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        profileService.delete(profileService.getById(id));

        return ResponseEntity.noContent().build();
    }
}
