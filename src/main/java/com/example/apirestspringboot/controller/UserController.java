package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/users")
public class UserController {

    @PostMapping("/")
    public void create(@RequestBody User User) {
        System.out.println(User.getEmail());
    }

}
