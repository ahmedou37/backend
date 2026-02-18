package com.example.demo.service;

import java.security.Key;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.model.AuthToken;
import com.example.demo.model.MyUserDetails;
import com.example.demo.model.RefreshToken;
import com.example.demo.repositry.RefreshTokenRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class JWTService {

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    
    private String secretKey="sadfadsfaaaaaaaaaaasdfffffffffffffweeeeeeefjaaaaaasfdjjjjkkm";

    @Transactional
    public AuthToken generateTokenAndRefreshToken(String username   , Collection<? extends GrantedAuthority> authorities , HttpServletRequest request) {
    Map<String, Object> claims = new HashMap<>();


        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        
        claims.put("roles", roles);


        long issuedAtTime = System.currentTimeMillis();
        long expirationTime = issuedAtTime + 1000 * 60 * 30 ;

        boolean isTokenExpired = System.currentTimeMillis() > expirationTime;

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
            .setExpiration(new Date(expirationTime + 1000L * 60 * 60 * 24 * 30))
            .signWith(getKey())
            .compact();
        
        
        refreshTokenRepo.save(new RefreshToken( username , refreshToken, LocalDate.now().plusMonths(1)));
        

        return new AuthToken(token, "Bearer", new Date(expirationTime), isTokenExpired , refreshToken);
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        
        claims.put("roles", roles);

        long issuedAtTime = System.currentTimeMillis();
        long expirationTime = issuedAtTime + 1000 * 60 * 30; 

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(issuedAtTime))
            .setExpiration(new Date(expirationTime)) 
            .signWith(getKey())
            .compact();
    }

    public boolean validateRefreshToken(String refreshToken) {
        Claims claims = extractAllClaims(refreshToken);
        String username = claims.getSubject();
        RefreshToken newRefreshToken = refreshTokenRepo.findByToken(refreshToken);
        MyUserDetails user = (MyUserDetails) myUserDetailsService.loadUserByUsername(username);
        
        if (!(newRefreshToken == null ||newRefreshToken.isRevoked() || !newRefreshToken.getToken().equals(refreshToken) || isTokenExpired(refreshToken) || !user.isAccountNonLocked())) {
            return true;
        }

        throw new RuntimeException("Invalid refresh token");
    }

    public String revokeUserRefreshTokens(String username) {
        List<RefreshToken> refreshTokens = refreshTokenRepo.findAllByUsername(username);
        if (refreshTokens == null || refreshTokens.isEmpty()) {
            return "no refresh tokens found for the user";
        }
        for (RefreshToken token : refreshTokens) {
            token.setRevoked(true);
            refreshTokenRepo.save(token);
        }
        return "refresh tokens revoked successfully";
    }

    public String unrevokeUserRefreshTokens(String username) {
        List<RefreshToken> refreshTokens = refreshTokenRepo.findAllByUsername(username);
        if (refreshTokens == null || refreshTokens.isEmpty()) {
            return "no refresh tokens found for the user";
        }
        for (RefreshToken token : refreshTokens) {
            token.setRevoked(false);
            refreshTokenRepo.save(token);
        }
        return "refresh tokens unrevoked successfully";
    }

    public String deleteRefreshToken(String username) {
        List<RefreshToken> tokens = refreshTokenRepo.findAllByUsername(username);
        if (tokens != null && !tokens.isEmpty()) {
            refreshTokenRepo.deleteAll(tokens);
            return "refresh tokens deleted successfully";
        } else {
            return "refresh token not found";
        }
       
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
                .setSigningKey(getKey())
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