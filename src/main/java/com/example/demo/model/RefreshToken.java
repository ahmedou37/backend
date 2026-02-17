package com.example.demo.model;

import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id ;
    String username;
    @Column(length = 2000)
    String token;
    LocalDate expiryDate;
    boolean revoked;

    public RefreshToken(String username,String refreshToken, LocalDate expiryDate) {
        this.username = username;
        this.expiryDate = expiryDate;
        this.revoked = false;
        this.token = refreshToken;
    }
}
