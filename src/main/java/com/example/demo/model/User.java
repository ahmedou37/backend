package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")   
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private String email;
    private String imageName;

    @JsonIgnore
    @OneToMany(mappedBy ="assignedUser",cascade = CascadeType.REMOVE)
    @OrderBy("updatedAt DESC")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    @OrderBy("updatedAt DESC")
    private List<Notification> notifications;  
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    public enum Role {
        USER,
        ADMIN
    }
}
