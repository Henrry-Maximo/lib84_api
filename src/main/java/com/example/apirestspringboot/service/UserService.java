package com.example.apirestspringboot.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.apirestspringboot.dto.UserPatchDto;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.exception.DuplicateEmailException;
import com.example.apirestspringboot.exception.UserByEmailNotFoundException;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(UserRecordDto dto) {
        var userAlreadyExists = this.userRepository.findByEmail(dto.email());

        if (userAlreadyExists != null) {
            throw new DuplicateEmailException(dto.email());
        }

        var user = new User();

        user.setEmail(dto.email());
        user.setPassword(BCrypt.withDefaults().hashToString(12, dto.password().toCharArray()));
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

        if (user == null) {
            throw new UserByEmailNotFoundException(email);
        }

        return user;
    }

    @Transactional
    public User update(UUID id, UserRecordDto dto) {
        var user = this.getById(id);

        user.setEmail(dto.email());
        user.setPassword(BCrypt.withDefaults().hashToString(12, dto.password().toCharArray()));

        return this.userRepository.save(user);
    }

    @Transactional
    public User updateSameFields(UUID id, UserPatchDto dto) {
        var user = this.getById(id);

        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPassword(BCrypt.withDefaults().hashToString(12, dto.password().toCharArray()));
        if (dto.role() != null) user.setRole(User.Role.valueOf(dto.role()));

        return this.userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        var user = this.getById(id);

        this.userRepository.delete(user);
    }
}
