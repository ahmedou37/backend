package com.example.demo.exception;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExcepionHandler {

    @ExceptionHandler(value={RequestException.class})
    public ResponseEntity<Object> handleException(RequestException e){
        Exception apiException =new Exception(e.getMessage(), HttpStatus.BAD_REQUEST, null);

        return new ResponseEntity<>(apiException,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Exception apiException = new Exception("Validation failed", HttpStatus.BAD_REQUEST, errors);
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
