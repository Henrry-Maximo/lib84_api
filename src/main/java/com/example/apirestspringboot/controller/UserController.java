package com.example.apirestspringboot.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.UserRepository;
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
@RequestMapping("/users")
@Tag(name = "Users", description = "User management, featuring options such as listing, searching, creating, updating, and deleting.")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public List<User> all() {
        return this.userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping("/")
    public ResponseEntity<User> create(@RequestBody @Valid UserRecordDto userRecordDto) {

        var user = new User();
        BeanUtils.copyProperties(userRecordDto, user);

        var userAlreadyExists = this.userRepository.findByEmail(user.getEmail());

        if (userAlreadyExists != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var passwordHash = BCrypt.withDefaults()
                .hashToString(12, user.getPassword().toCharArray());

        user.setPassword(passwordHash);

        var userCreated = this.userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PutMapping("/{id}")
    public Optional<User> updateAllFields(@RequestBody User newUser, @PathVariable UUID id) {
        return this.userRepository.findById(id).map(user -> {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRole(newUser.getRole());

            return this.userRepository.save(user);
        });
    }

    @PatchMapping("/{id}")
    public Optional<User> updateSameFields(@RequestBody User newUser, @PathVariable UUID id) {
        return this.userRepository.findById(id).map(user -> {
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRole(newUser.getRole());

            return this.userRepository.save(user);
        });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        this.userRepository.deleteById(id);
    }
}
