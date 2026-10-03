package com.example.apirestspringboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class AuthorController {

    @GetMapping("/authors")
    public String hello() {
        return "rota de autores";
    }

}
