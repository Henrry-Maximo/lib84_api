package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Entity()
@Table(name = "tb_authors")
@JsonPropertyOrder({"id", "name", "biography", "_links"})
public class Author extends RepresentationModel<Author> {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 155)
    @Size(max = 155)
    private String name;

    @Size(max = 255)
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
