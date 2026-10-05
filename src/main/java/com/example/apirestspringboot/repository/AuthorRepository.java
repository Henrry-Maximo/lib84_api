package com.example.apirestspringboot.repository;

import com.example.apirestspringboot.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findByNameContainingIgnoreCase(String name);
}
