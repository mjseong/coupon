package com.assignment.coupon.domain.dto;

import org.springframework.format.annotation.DateTimeFormat;

public class IssueDto {
    private long count;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String expireDate;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
