package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "2df59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 155)
    @Column(nullable = false, unique = true, length = 155)
    @Schema(example = "george orwell")
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    @Schema(example = "Romancista, jornalista e ensaísta inglês.")
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
