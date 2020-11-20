package com.assignment.coupon.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponTests {

    @Autowired
    CouponService couponService;

    @Test
    void issueCoupon() {
        long count = couponService.createCoupon(10000, null).getCouponCount();
        Assertions.assertEquals(10000, count);
    }


}
