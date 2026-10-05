package com.example.apirestspringboot.repository;

import com.example.apirestspringboot.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByCategoryId(UUID categoryId);
}
