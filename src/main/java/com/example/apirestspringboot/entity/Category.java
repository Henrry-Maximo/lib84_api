package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Entity
@Table(name = "tb_categories")
@JsonPropertyOrder({"id", "title", "description", "_links"})
public class Category extends RepresentationModel<Category> {

    @Id
    @GeneratedValue(generator = "UUID")
    @Schema(example = "3ac59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    @Schema(example = "Engenharia de Software")
    private String title;

    @Size(max = 500)
    @Column(length = 500)
    @Schema(example = "Livros sobre design de software e arquitetura")
    private String description;

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
