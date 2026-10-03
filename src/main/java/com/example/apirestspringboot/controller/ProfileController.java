package com.example.apirestspringboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class ProfileController {

    @GetMapping("/profile")
    public String hello() {
        return "rota de perfil";
    }

}
