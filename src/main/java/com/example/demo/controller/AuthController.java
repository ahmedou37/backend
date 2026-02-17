package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.AuthToken;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public AuthToken login(@RequestBody User user , HttpServletRequest request){
        return authService.login(user,request);
    }

    @PostMapping("/refresh")
    public String getNewAccessToken(@RequestBody String refreshToken){
        return authService.getNewAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public String logout(Authentication authentication){
        return authService.logout(authentication);
    }
}
