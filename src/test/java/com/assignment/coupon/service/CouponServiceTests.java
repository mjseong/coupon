package com.assignment.coupon.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponServiceTests {

    @Autowired
    CouponService couponService;

    @Test
    void issueCoupon() {
        long count = couponService.createCoupon(1000, Instant.now().plus(Duration.ofDays(2))).getCouponCount();
        Assertions.assertEquals(1000, count);
    }


}
