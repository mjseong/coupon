package com.assignment.coupon.service;


import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.repository.CouponRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAsync
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AsyncTaskServiceTests {

    @Autowired
    CouponService couponService;

    @Autowired
    AsyncTaskService asyncTaskService;

    @Test
    public void expirationCallTest() throws InterruptedException, ExecutionException {

        Instant to = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant from = to.minus(Duration.ofDays(2));

        long act = couponService.findCouponsByExpireDateBetweenAndState(from,to, EnumCouponState.EXPIRE.getState()).size();
        long expected = act;

        expected += couponService.createCoupon(10, Instant.now().minus(Duration.ofDays(1))).size();

        //TODO: @async test방법 찾다가 해결함. 시간날때 검토해야
        // Awaitility 사용하였음. + ref: https://stackoverflow.com/questions/57869463/testing-async-annotated-method-in-springboottest
        CompletableFuture<Boolean> result = asyncTaskService.expireCoupons();
        await().atMost(5, TimeUnit.SECONDS).until(result::isDone);
        Assertions.assertTrue(result.get());

        act = couponService.findCouponsByExpireDateBetweenAndState(from,to, EnumCouponState.EXPIRE.getState()).size();

        Assertions.assertEquals(expected, act);

    }

    @Test
    public void noticeCouponTest() throws ExecutionException, InterruptedException {
        Instant formExpDate = Instant.now().plus(Duration.ofDays(3)).truncatedTo(ChronoUnit.DAYS);
        Instant toExpireDate = formExpDate.plus(Duration.ofDays(1));

        CompletableFuture<Long> result = asyncTaskService.notice3DaysExpirationCoupons();

        long count = couponService.findCouponsByExpireDateBetweenAndState(formExpDate, toExpireDate, EnumCouponState.ASSIGN.getState()).size();
        await().atMost(5, TimeUnit.SECONDS).until(result::isDone);

        Assertions.assertEquals( count , result.get());
    }

    // example ref: https://stackoverflow.com/questions/57869463/testing-async-annotated-method-in-springboottest
    private CompletableFuture<Boolean> getCompleteResult(){
        CompletableFuture<Boolean> result = asyncTaskService.expireCoupons();
        await().atMost(10, TimeUnit.SECONDS).until(result::isDone);
        return result;
    }



}
