package com.example.apirestspringboot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Tag(name = "Loans", description = "Loan management, featuring options such as listing, searching, creating, updating, and deleting.")
public class LoanController {

    @GetMapping("/loans")
    public String hello() {
        return "rota de empréstimo";
    }

}
