package com.example.demo.configuration;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.model.MyUserDetails;

@Configuration
public class GlobalConfig {
    
    @Bean
    public AuditorAware<Integer> auditor() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
                return Optional.of(userDetails.getUser().getId());
            }
            return Optional.empty();
        };
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }

    @CacheEvict(value={"users","tasks","usersById","usertasks"}, allEntries = true)
    @Scheduled(fixedRate = 200000)
    public void clearCaches() {
        System.out.println("Caches Cleared");
    }
}
