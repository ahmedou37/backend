package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.model.AuthToken;
import com.example.demo.model.MyUserDetails;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {
    
    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Autowired
    JWTService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthToken login(User user , HttpServletRequest request) {
        Authentication authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
        if(user.isLocked()){
            throw new RuntimeException("account is locked");
        }

        if (authentication.isAuthenticated()) {

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

            return jwtService.generateTokenAndRefreshToken(user.getName(), userDetails.getAuthorities(),request);
        }
        
        throw new RuntimeException("invalid username or password");
    }

    public String getNewAccessToken(String refreshToken){
        if (jwtService.validateRefreshToken(refreshToken)) {
            String username = jwtService.extractUserName(refreshToken);
            MyUserDetails userDetails = (MyUserDetails) myUserDetailsService.loadUserByUsername(username);
            return jwtService.generateToken(username, userDetails.getAuthorities());
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public String logout(Authentication authentication){
        String username = authentication.getName();
        jwtService.deleteRefreshToken(username);
        authentication.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(null);
        return "logged out successfully";
    }
}
