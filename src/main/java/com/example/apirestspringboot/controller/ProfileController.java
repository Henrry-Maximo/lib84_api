package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.ProfileModelAssembler;
import com.example.apirestspringboot.dto.ProfileRecordDto;
import com.example.apirestspringboot.entity.Profile;
import com.example.apirestspringboot.service.ProfileService;
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
@RequestMapping("/profiles")
@Tag(name = "Profile", description = "Profile management, featuring options such as listing, searching, creating, updating, and deleting.")
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileModelAssembler assembler;
    private final PagedResourcesAssembler<Profile> pagedResourcesAssembler;

    public ProfileController(ProfileService profileService, ProfileModelAssembler assembler, PagedResourcesAssembler<Profile> pagedResourcesAssembler) {
        this.profileService = profileService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all profiles")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Profile>>> all(@ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Profile> profiles = this.profileService.getAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(profiles, assembler));
    }

    @Operation(summary = "Get a profile by id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Profile>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(profileService.getById(id)));
    }

    @Operation(summary = "Create a profile")
    @PostMapping
    public ResponseEntity<EntityModel<Profile>> create(@RequestBody @Valid ProfileRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(profileService.create(dto)));
    }

    @Operation(summary = "Update a profile")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Profile>> update(@RequestBody @Valid ProfileRecordDto dto, @PathVariable UUID id) {
        Profile profile = profileService.getById(id);
        return ResponseEntity.ok(assembler.toModel(profileService.update(profile, dto)));
    }

    @Operation(summary = "Delete a profile")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        profileService.delete(profileService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
