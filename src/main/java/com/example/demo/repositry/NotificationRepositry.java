package com.example.demo.repositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Notification;

@Repository
public interface NotificationRepositry extends JpaRepository<Notification,Integer> {
    List<Notification> findAllByOrderByUpdatedAtDesc();
}
