package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.events.UserSignUp;

@Service
public class EmailService {

    @Autowired(required= false)
    private JavaMailSender javaMailSender;

    @Async
    @EventListener
    public String sendEmailOnUserSignUp(UserSignUp event) {
        // SimpleMailMessage mailMessage=new SimpleMailMessage();
        // mailMessage.setTo(event.getUser().getEmail());
        // mailMessage.setSubject("You Signed Up");
        // mailMessage.setText("you signup succefly ! you can now login at any time");
        // javaMailSender.send(mailMessage);
        System.out.println("email sent to: " + event.getUser().getEmail());
        return "email sent";
    }
}
