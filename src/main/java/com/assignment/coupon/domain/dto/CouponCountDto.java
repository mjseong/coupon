package com.assignment.coupon.domain.dto;


public class CouponCountDto {
     public CouponCountDto(long count){this.couponCount = count;}
    long couponCount = 0;

    public long getCouponCount() {
        return couponCount;
    }
}
