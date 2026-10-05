package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.AuthorRecordDto;
import com.example.apirestspringboot.entity.Author;
import com.example.apirestspringboot.exception.AuthorNotFoundException;
import com.example.apirestspringboot.repository.AuthorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author create(AuthorRecordDto dto) {
        var author = new Author();
        BeanUtils.copyProperties(dto, author);
        return this.authorRepository.save(author);
    }

    public Page<Author> getAll(Pageable pageable) {
        return this.authorRepository.findAll(pageable);
    }

    public Author getById(UUID id) {
        return this.authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
    }

    public Author update(Author author, AuthorRecordDto dto) {
        BeanUtils.copyProperties(dto, author);
        return this.authorRepository.save(author);
    }

    public Author getByName(String name) {
        return this.authorRepository.findByNameContainingIgnoreCase(name).orElseThrow(() -> new AuthorNotFoundException(null));
    }

    public void delete(Author author) {
        this.authorRepository.deleteById(author.getId());
    }
}
