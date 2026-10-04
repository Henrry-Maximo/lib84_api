package com.example.apirestspringboot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Tag(name = "Profile", description = "Profile management, featuring options such as listing, searching, creating, updating, and deleting.")
public class ProfileController {

    @GetMapping("/profile")
    public String hello() {
        return "rota de perfil";
    }

}
