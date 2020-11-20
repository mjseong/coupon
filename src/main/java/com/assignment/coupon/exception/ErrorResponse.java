package com.assignment.coupon.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {

    protected String code;
    protected String message;
    protected T content;

    public <K> ErrorResponse(String code, String message, K content) {
        this.code = code;
        this.message = message;
        this.content = (T) content;
    }

    public static <K> ErrorResponse<K> body(String code, String message, K content){
        return new ErrorResponse(code, message, content);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getContent() {
        return content;
    }
}
