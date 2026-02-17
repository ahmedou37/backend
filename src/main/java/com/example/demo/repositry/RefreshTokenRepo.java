package com.example.demo.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RefreshToken;

import java.util.List;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer> {
    RefreshToken findByToken(String token);
    RefreshToken findByUsername(String username);
    List<RefreshToken> findAllByUsername(String username);
}
