package com.assignment.coupon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity handleAlreadyExistsException(AlreadyExistsException aeE){
        return new ResponseEntity(aeE.getMessage(), HttpStatus.CONFLICT);
    }
}
