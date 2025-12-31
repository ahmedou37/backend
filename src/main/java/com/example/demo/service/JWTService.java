package com.example.demo.service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.model.AuthenticationToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JWTService {
    
    private String secretKey="sadfadsfaaaaaaaaaaasdfffffffffffffweeeeeeefjaaaaaasfdjjjjkkm";

    public AuthenticationToken generateToken(String username   , Collection<? extends GrantedAuthority> authorities , HttpServletRequest request) {
    Map<String, Object> claims = new HashMap<>();


        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        
        claims.put("roles", roles);


        long issuedAtTime = System.currentTimeMillis();
        long expirationTime = issuedAtTime + 1000 * 60 * 60 * 10;
        long tokenExp = expirationTime - issuedAtTime;

        long refreshTokenExp=tokenExp+ 1000 * 60 * 60 * 2;

        String token= Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(issuedAtTime))
            .setExpiration(new Date(expirationTime)) 
            .signWith(getKey())
            .compact();

        String refreshToken= Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(issuedAtTime))
            .setNotBefore(new Date(expirationTime))
            .setExpiration(new Date(expirationTime + 1000 * 60 * 60 * 2))
            .signWith(getKey())
            .compact();
        
        boolean isTokenExpired = System.currentTimeMillis() > expirationTime;

        return new AuthenticationToken(token, "Bearer", tokenExp, isTokenExpired , refreshToken, refreshTokenExp);
    }


    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    
    public String extractUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    


    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        
        if (roles == null) {
            return new ArrayList<>();
        }
        
        return roles;
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        List<String> roles = extractRoles(token);
        
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());
    }
}