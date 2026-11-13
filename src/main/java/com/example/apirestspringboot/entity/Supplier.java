package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_suppliers")
@JsonPropertyOrder({"id", "name", "email", "phone", "cnpj", "createdAt", "_links"})
public class Supplier extends RepresentationModel<Supplier> {

    @Id
    @GeneratedValue(generator = "UUID")
    @Schema(example = "1bd59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private UUID id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    @Schema(example = "Editora Exemplo Ltda")
    private String name;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    @Schema(example = "contato@editoraexemplo.com")
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    @Schema(example = "+55 11 3333-4444")
    private String phone;

    @Size(max = 18)
    @Column(length = 18)
    @Schema(example = "12.345.678/0001-95")
    private String cnpj;

    @CreationTimestamp
    @Schema(example = "2026-11-13T00:00:00")
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
