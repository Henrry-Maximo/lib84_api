package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/")
    public User create(@RequestBody User user) {
        var userAlreadyExists = this.userRepository.findByEmail(user.getEmail());

        if (userAlreadyExists != null) {
            System.out.println("Usuário já existe.");
            return null;
        }

        var userCreated = this.userRepository.save(user);
        return userCreated;

        // System.out.println(User.getEmail());
    }

}
