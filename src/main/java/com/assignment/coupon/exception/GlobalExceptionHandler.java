package com.assignment.coupon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity handleAlreadyExistsException(AlreadyExistsException aeE){
        return new ResponseEntity(ErrorResponse.body("409",aeE.getMessage(), null), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = CouponServiceException.class)
    public ResponseEntity handleCouponServiceException(CouponServiceException csE){
        return new ResponseEntity(ErrorResponse.body("500",csE.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
