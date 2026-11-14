package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.UserPatchDto;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.exception.DuplicateEmailException;
import com.example.apirestspringboot.exception.UserByEmailNotFoundException;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(UserRecordDto dto) {
        var existing = this.userRepository.findByEmail(dto.email());

        if (existing.isPresent()) {
            throw new DuplicateEmailException(dto.email());
        }

        var user = new User();
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(User.Role.MEMBER);

        return this.userRepository.save(user);
    }

    public Page<User> getAll(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public User getById(UUID id) {
        var user = this.userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        return user.get();
    }

    public User getByEmail(String email) {
        var user = this.userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UserByEmailNotFoundException(email);
        }

        return user.get();
    }

    @Transactional
    public User update(UUID id, UserRecordDto dto) {
        var user = this.getById(id);

        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return this.userRepository.save(user);
    }

    @Transactional
    public User updateSameFields(UUID id, UserPatchDto dto) {
        var user = this.getById(id);

        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.role() != null) user.setRole(User.Role.valueOf(dto.role()));

        return this.userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        var user = this.getById(id);

        this.userRepository.delete(user);
    }
}
