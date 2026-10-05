package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.asssembler.UserModelAssembler;
import com.example.apirestspringboot.dto.UserPatchDto;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@RestController()
@Tag(name = "Users", description = "User management, featuring options such as listing, searching, creating, updating, and deleting.")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;
    private final PagedResourcesAssembler<User> pagedResourcesAssembler;

    public UserController(UserService userService, UserModelAssembler assembler, PagedResourcesAssembler<User> pagedResourcesAssembler) {
        this.userService = userService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Returned a paginated list of all visible users")
    @GetMapping("/users")
    public ResponseEntity<PagedModel<EntityModel<User>>> all(@ParameterObject @PageableDefault(size = 10, page = 0, sort = "email") Pageable pageable) {
        Page<User> users = this.userService.getAll(pageable);
        PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(users, this.assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Get a user by your id")
    @ApiResponse(responseCode = "200", description = "Returned a object visible user", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<User>> getUserById(@PathVariable(value = "id") UUID id) {
        User user = this.userService.getById(id);

        EntityModel<User> entityModel = assembler.toModel(user);
        return ResponseEntity.ok(entityModel);

        /*
        if (user.isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        user.get().add(linkTo(methodOn(UserController.class).all()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(user.get());

        return user.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
         */
    }

    @Operation(summary = "Search user by email")
    @GetMapping("/users/search")
    public ResponseEntity<EntityModel<User>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(assembler.toModel(userService.getByEmail(email)));
    }

    @PostMapping("/users/")
    public ResponseEntity<EntityModel<User>> create(@RequestBody @Valid UserRecordDto dto) {
        User userCreated = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(userCreated));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<EntityModel<User>> updateAllFields(@RequestBody @Valid UserRecordDto userRecordDto, @PathVariable(value = "id") UUID id) {
        User user = this.userService.getById(id);

        User updated = this.userService.update(user, userRecordDto);

        // EntityModel<User> entityModel = assembler.toModel(user);
        return ResponseEntity.ok(assembler.toModel(updated));

        /*
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        var userEntity = user.get();

        BeanUtils.copyProperties(userRecordDto, userEntity);
        return this.userRepository.findById(id).map(user -> {
           user.setEmail(newUser.getEmail());
           user.setPassword(newUser.getPassword());
           user.setRole(newUser.getRole());

           return this.userRepository.save(user);
        });
         */
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<EntityModel<User>> updateSameFields(@RequestBody @Valid UserPatchDto dto, @PathVariable UUID id) {
        User user = this.userService.getById(id);
        User updated = this.userService.updateSameFields(user, dto);

        return ResponseEntity.ok(assembler.toModel(updated));

        /*
        return this.userRepository.findById(id).map(user -> {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRole(newUser.getRole());

            return this.userRepository.save(user);
        });
         */
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") UUID id) {
        User user = this.userService.getById(id);

        this.userService.delete(user);

        return ResponseEntity.noContent().build();

        // return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        // próprio delete do JPA, passando a entidade
        // this.userRepository.delete(user.get());
        // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User deleted successfully.");

        // this.userRepository.deleteById(id);
    }
}
