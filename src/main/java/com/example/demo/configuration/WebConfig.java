package com.example.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {//WebMvcConfigurer is a Spring interface that allows you to customize Spring MVC configuration without using XML

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //This makes Spring serve files in /images folder under the URL /images/**
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:images/");
    }
}
