package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Entity(name = "tb_categories")
@JsonPropertyOrder({"id", "title", "description", "_links"})
public class Category extends RepresentationModel<Category> {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String title;

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
