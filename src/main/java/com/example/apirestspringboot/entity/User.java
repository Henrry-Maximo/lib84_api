package com.example.apirestspringboot.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class User {
    public enum Role {
        MEMBER,
        ADMIN
    }

    @Id @GeneratedValue
    private UUID id;

    private String email;
    private String password;
    @Enumerated(EnumType.STRING) // armazenar enum como string
    private Role role;
    private LocalDateTime dateCreation;

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
