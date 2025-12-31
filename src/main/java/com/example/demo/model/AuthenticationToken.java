package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationToken {
    String token  ;
    String headerType;
    long tokenExp;
    boolean isTokenExpired;
    String refreshToken;
    long refreshTokenExp;    
}
