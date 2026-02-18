package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) 
public class Task {
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
  
    public Task(String title  ){
        this.title=title;
    }
  
    public void setId(int id) {
        this.id = id;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status = Status.Pending;
    private LocalDate deadline;

    @ManyToOne
    private User assignedUser;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private int version;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDate createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date",insertable = false , nullable = true)
    private LocalDate lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false )
    private Integer createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", insertable = false , nullable = true)
    private Integer lastModifiedBy;


    public enum Status {
        Pending,
        In_Progress,
        Completed,
        Overdue
    }

}
