package com.example.apirestspringboot.controller;

import com.example.apirestspringboot.entity.Book;
import com.example.apirestspringboot.repository.BookRepository;
import com.example.apirestspringboot.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController()
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management, featuring options such as listing, searching, creating, updating, and deleting.")
public class BookController {

    // injeção de dependência
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public Collection<Book> getBook() {
        return this.bookRepository.findAll();
    }

    @PostMapping("/")
    public void createBook(@RequestBody Book book) {
        this.bookRepository.save(book);
    }

}
