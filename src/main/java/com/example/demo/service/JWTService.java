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


    // Extract role names from authorities and add to claims
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
            .setIssuedAt(new Date(issuedAtTime))//Stores the exact time the token was created.
            .setExpiration(new Date(expirationTime)) //After this date, the token is considered invalid → 401 Unauthorized
            .signWith(getKey())//add a key , so only your server can create valid tokens (has the key)
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
        return claims.getSubject();// extractClaim(token, Claims::getSubject/*claims -> claims.getSubject()*/);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()//It returns a JwtParserBuilder object that lets you configure the rules for parsing JWT tokens before actually parsing them
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)//throw exeption if the token or signature is invalide
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
        
        @SuppressWarnings("unchecked")//Compiler warning: "Unchecked conversion from List to List<String>"
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