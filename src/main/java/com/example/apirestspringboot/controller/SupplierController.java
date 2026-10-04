package com.example.apirestspringboot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Tag(name = "Suppliers", description = "Supplier management, featuring options such as listing, searching, creating, updating, and deleting.")
public class SupplierController {

    @GetMapping("/suppliers")
    public String hello() {
        return "rota de fornecedores";
    }

}
