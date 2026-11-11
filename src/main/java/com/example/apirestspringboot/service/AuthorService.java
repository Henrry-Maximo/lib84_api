package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.AuthorRecordDto;
import com.example.apirestspringboot.entity.Author;
import com.example.apirestspringboot.exception.AuthorAlreadyExistsException;
import com.example.apirestspringboot.exception.AuthorByNameNotFoundException;
import com.example.apirestspringboot.exception.AuthorNotFoundException;
import com.example.apirestspringboot.repository.AuthorRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public Author create(AuthorRecordDto dto) {
        var name = normalizeName(dto.name());

        var authorAlreadyExists = this.authorRepository.existsByName(name);

        if (authorAlreadyExists) {
            throw new AuthorAlreadyExistsException(name);
        }

        var author = new Author();

        author.setName(name);
        author.setBiography(dto.biography());

        return this.authorRepository.save(author);
    }

    public Page<Author> getAll(Pageable pageable) {
        return this.authorRepository.findAll(pageable);
    }

    public Author getById(UUID id) {
        return this.authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
    }

    @Transactional
    public Author update(UUID id, AuthorRecordDto dto) {
        var author = this.getById(id);

        var name = normalizeName(dto.name());

        var authorAlreadyExists = !author.getName().equals(name)
                && this.authorRepository.existsByName(name);

        if (authorAlreadyExists) {
            throw new AuthorAlreadyExistsException(name);
        }

        author.setName(name);
        author.setBiography(dto.biography());

        return this.authorRepository.save(author);
    }

    public Author getByName(String name) {
        var normalizedName = normalizeName(name);

        return this.authorRepository.findByName(normalizedName)
                .orElseThrow(() -> new AuthorByNameNotFoundException(normalizedName));
    }

    @Transactional
    public void delete(UUID id) {
        var author = this.getById(id);

        this.authorRepository.delete(author);
    }

    private String normalizeName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}