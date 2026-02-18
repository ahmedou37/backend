package com.example.demo.events;

import java.time.LocalDateTime;

import com.example.demo.model.UserDTO;

import lombok.Data;

@Data
public class UserSignUp {
    private UserDTO user;
    private LocalDateTime signUpTime;

    public UserSignUp(UserDTO user) {
        this.user = user;
        this.signUpTime = LocalDateTime.now();
    }
}
