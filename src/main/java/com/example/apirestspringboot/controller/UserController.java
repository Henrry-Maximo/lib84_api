package com.example.apirestspringboot.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.UserRepository;
import com.example.apirestspringboot.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController()
//@RequestMapping("/users")
@Tag(name = "Users", description = "User management, featuring options such as listing, searching, creating, updating, and deleting.")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> all() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable(value="id") UUID id) {
        Optional<User> user = this.userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(user.get());

        // return user.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
        // return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping("/users/")
    public ResponseEntity<User> create(@RequestBody @Valid UserRecordDto dto) {
        User userCreated = userService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userCreated);
    }

    @PutMapping("/users/{id}")
    public Optional<User> updateAllFields(@RequestBody User newUser, @PathVariable UUID id) {
        return this.userRepository.findById(id).map(user -> {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRole(newUser.getRole());

            return this.userRepository.save(user);
        });
    }

    @PatchMapping("/users/{id}")
    public Optional<User> updateSameFields(@RequestBody User newUser, @PathVariable UUID id) {
        return this.userRepository.findById(id).map(user -> {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRole(newUser.getRole());

            return this.userRepository.save(user);
        });
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable UUID id) {
        this.userRepository.deleteById(id);
    }
}
