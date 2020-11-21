package com.assignment.coupon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity handleAlreadyExistsException(AlreadyExistsException aeE){
        return new ResponseEntity(ErrorResponse.body("409",aeE.getMessage(), null), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = CouponServiceException.class)
    public ResponseEntity handleCouponServiceException(CouponServiceException csE){
        log.error(csE.getMessage());
        return new ResponseEntity(ErrorResponse.body("500",csE.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity handleNoSuchElementException(NoSuchElementException neE){
        return new ResponseEntity(ErrorResponse.body("404", neE.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    public ResponseEntity handleDateTimeParseException(DateTimeParseException dpE){
        return new ResponseEntity(ErrorResponse.body("400", dpE.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException cvE){
        return new ResponseEntity(ErrorResponse.body("400", "Bad reqiest parameter", null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e){
        log.error(e.getMessage());
        return new ResponseEntity(ErrorResponse.body("500", "system error :  please call coupon_manager team ", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
