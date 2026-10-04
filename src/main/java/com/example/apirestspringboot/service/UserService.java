package com.example.apirestspringboot.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.apirestspringboot.dto.UserRecordDto;
import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(UserRecordDto dto) {

        var user = new User();
        BeanUtils.copyProperties(dto, user);

        var userAlreadyExists =
                userRepository.findByEmail(user.getEmail());

        if (userAlreadyExists != null) {
            throw new RuntimeException("Usuário já existe");
        }

        var passwordHash = BCrypt.withDefaults()
                .hashToString(12, user.getPassword().toCharArray());

        user.setPassword(passwordHash);

        return userRepository.save(user);
    }
}
