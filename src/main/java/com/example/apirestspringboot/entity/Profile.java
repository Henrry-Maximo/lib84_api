package com.example.apirestspringboot.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Entity
@Table(name = "tb_profiles")
@JsonPropertyOrder({"id", "name", "phone", "address", "user", "_links"})
public class Profile extends RepresentationModel<Profile> {

    @Id
    @GeneratedValue(generator = "UUID")
    @Schema(example = "2df59133-1c5f-4ae4-b0be-b657c68b4dc3")
    private UUID id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    @Schema(example = "John Doe")
    private String name;

    @Size(max = 20)
    @Column(length = 20)
    @Schema(example = "+55 11 91234-5678")
    private String phone;

    @Size(max = 255)
    @Column(length = 255)
    @Schema(example = "Rua das Flores, 123 - Sao Paulo/SP")
    private String address;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
