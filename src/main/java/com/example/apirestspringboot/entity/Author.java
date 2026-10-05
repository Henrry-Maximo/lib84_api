package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Entity(name = "tb_authors")
@JsonPropertyOrder({"id", "name", "biography", "_links"})
public class Author extends RepresentationModel<Author> {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String name;
    private String biography;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
