package com.assignment.coupon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserCredentialNotValidException.class)
    public ResponseEntity handleCredentialNotVaild(UserCredentialNotValidException ucNVE){
        return new ResponseEntity(ucNVE.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
