package com.assignment.coupon.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponDto {

    String couponCode;
    String issuer;
    String userName;
    String state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    Instant createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    Instant exprieDate;

    public CouponDto(String couponCode, String issuer, String state, String userName, Instant createDate, Instant exprieDate) {
        this.couponCode = couponCode;
        this.issuer = issuer;
        this.state = state;
        this.userName = userName;
        this.createDate = createDate;
        this.exprieDate = exprieDate;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getExprieDate() {
        return exprieDate;
    }

    public void setExprieDate(Instant exprieDate) {
        this.exprieDate = exprieDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
