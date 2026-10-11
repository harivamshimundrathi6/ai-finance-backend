package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String businessName;
    private LocalDateTime createdAt;

    public Merchant() {
        this.createdAt = LocalDateTime.now();
    }

    public Merchant(String email, String password, String businessName) {
        this.email = email;
        this.password = password;
        this.businessName = businessName;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}