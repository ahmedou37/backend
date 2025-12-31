package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.User.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")   
public class UserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    private String name;
    private Role role = Role.USER;
    private String email;
    private String imageName;

    @JsonIgnore
    @OneToMany(mappedBy ="assignedUser")
    private List<Task> tasks = new ArrayList<>();  
}
