package com.example.apirestspringboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class SupplierController {

    @GetMapping("/suppliers")
    public String hello() {
        return "rota de fornecedores";
    }

}
