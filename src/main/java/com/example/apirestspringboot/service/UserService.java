package com.example.apirestspringboot.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.apirestspringboot.dto.UserPatchDto;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.exception.DuplicateEmailException;
import com.example.apirestspringboot.exception.UserByEmailNotFoundException;
import com.example.apirestspringboot.exception.UserNotFoundException;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(UserRecordDto dto) {
        var user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setRole(User.Role.MEMBER);

        var userAlreadyExists = this.userRepository.findByEmail(user.getEmail());

        if (userAlreadyExists != null) {
            throw new DuplicateEmailException(user.getEmail());
        }

        var passwordHash = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());

        user.setPassword(passwordHash);

        return this.userRepository.save(user);
    }

    public Page<User> getAll(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public User getById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User update(User user, UserRecordDto dto) {
        /*
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        */

        BeanUtils.copyProperties(dto, user, "password");
        if (dto.password() != null) user.setPassword(BCrypt.withDefaults().hashToString(12, dto.password().toCharArray()));
        if (dto.role() != null) user.setRole(User.Role.valueOf(dto.role()));

        return this.userRepository.save(user);
    }

    public User getByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) throw new UserByEmailNotFoundException(email);

        return user;
    }

    public void delete(User user) {
        this.userRepository.deleteById(user.getId());
    }

    public User updateSameFields(User user, UserPatchDto dto) {
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPassword(BCrypt.withDefaults().hashToString(12, dto.password().toCharArray()));
        if (dto.role() != null) user.setRole(User.Role.valueOf(dto.role()));

        return this.userRepository.save(user);
    }
}
