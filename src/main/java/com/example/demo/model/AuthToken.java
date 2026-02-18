package com.example.demo.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {
    String token  ;
    String headerType;
    Date tokenExp;
    boolean isTokenExpired;
    String refreshToken;
}
