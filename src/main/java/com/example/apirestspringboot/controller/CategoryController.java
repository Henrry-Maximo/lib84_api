package com.example.apirestspringboot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Tag(name = "Categories", description = "Category management, featuring options such as listing, searching, creating, updating, and deleting.")
public class CategoryController {
    @GetMapping("/categories")
    public String hello() {
        return "rota de categorias";
    }
}
