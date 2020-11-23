package com.assignment.coupon.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.time.Instant;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponServiceTests {

    Logger logger = LoggerFactory.getLogger(CouponServiceTests.class);

    @Autowired
    CouponService couponService;

    @Test
    void issueCoupon() {
        StopWatch stopWatch = new StopWatch();

//        stopWatch.start();
        long count = couponService.createCoupon(100000, Instant.now().plus(Duration.ofDays(2))).size();
//        stopWatch.stop();
//        logger.info("couponService.createCoupon :" +stopWatch.getTotalTimeSeconds());
//        logger.info("couponService.createCoupon :" +stopWatch.prettyPrint());
        Assertions.assertEquals(100000, count);
    }


}
