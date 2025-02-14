package com.example.WebQlyKho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;


import java.time.LocalDateTime;

@Entity(name = "users")
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "user_name")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "full_name")
    private String fullname;
    @Column(name = "email")
    private String email;
    @Column(name = "role")
    private String role;
    @Column(name = "cccd")
    private String cccd;
    @Column(name = "bank_number")
    private String bankNumber;
    @Column(name = "status")
    private boolean status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
