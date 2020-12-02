package com.assignment.coupon.service;


import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        List<Coupon> couponList = couponService.createCoupon(3, Instant.now().plus(Duration.ofDays(3)));

        couponList.forEach(coupon -> couponService.assignCoupon(coupon.getCouponCode(), "smj"));

        /**
         *  비동기 thread 호출 테스트에 CompletableFuture를 꼭 사용해서 리턴 받도록 하자.
         *  Test시에 다른 Thread를 호출하여 Transaction이 이어지지 않아 테스트가 어려우므로 비동기때는 생성된 데이터는 찾아 삭제하는 코드로 개발하는게 나을듯
         */
        CompletableFuture<Long> result = asyncTaskService.notice3DaysExpirationCoupons();

        long count = couponService.findCouponsByExpireDateBetweenAndState(formExpDate, toExpireDate, EnumCouponState.ASSIGN.getState()).size();
        // 5초 안에 thread의 실행이 완료 되었다면 pass
        await().atMost(5, TimeUnit.SECONDS).until(result::isDone);

        /**
         * result -> CompletableFuture.get()은 동기화(blocking)되어 count와 비교할수 있게 된다. .get()을 호출 안하면 blocking되지 않아 test불가
         * get을 호출하지 않으면 nonBlocking된다 이건 실제 사용코드에서 NonBlocking 코드로 유지할떄 get호출은 지양해야함.
         */
        Assertions.assertEquals( count , result.get());
    }

    // example ref: https://stackoverflow.com/questions/57869463/testing-async-annotated-method-in-springboottest
    private CompletableFuture<Boolean> getCompleteResult(){
        CompletableFuture<Boolean> result = asyncTaskService.expireCoupons();
        await().atMost(10, TimeUnit.SECONDS).until(result::isDone);
        return result;
    }



}
