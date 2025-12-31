package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Notification;
import com.example.demo.repositry.NotificationRepositry;

@RestController
public class NotificationsController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public void sendNotification(@RequestBody String message) {
        System.out.println("New notification: " + message);
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    @Autowired
    NotificationRepositry notificationRepo;

    @GetMapping("/not")
    public List<Notification> getNot(){
       return notificationRepo.findAllByOrderByUpdatedAtDesc();
    }
}