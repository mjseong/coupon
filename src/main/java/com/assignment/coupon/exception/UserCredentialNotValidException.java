package com.assignment.coupon.exception;

import org.springframework.security.core.AuthenticationException;

public class UserCredentialNotValidException extends AuthenticationException {
    public UserCredentialNotValidException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserCredentialNotValidException(String msg) {
        super(msg);
    }
}
