package com.example.apirestspringboot.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity(name = "tb_profiles")
public class Profile {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String name;
    private String phone;
    private String address;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false, unique = true)
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
