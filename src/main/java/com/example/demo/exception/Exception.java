package com.example.demo.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Exception {
    private String message;
   // private Throwable throwable;
    private HttpStatus httpStatus;
    private Map<String, String> errors;
          
}
