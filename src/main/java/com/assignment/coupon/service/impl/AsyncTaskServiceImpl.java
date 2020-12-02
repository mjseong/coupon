package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.service.AsyncTaskService;
import com.assignment.coupon.service.CouponHistService;
import com.assignment.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsyncTaskServiceImpl implements AsyncTaskService {

    private final CouponService couponService;
    private final CouponHistService couponHistService;

    public AsyncTaskServiceImpl(CouponService couponService, CouponHistService couponHistService){
        this.couponService = couponService;
        this.couponHistService = couponHistService;
    }

    @Async(value = "taskExpireExecutor")
    public CompletableFuture<Boolean> expireCoupons(){

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        Instant toExpDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant formExpDate = toExpDate.minus(Duration.ofDays(3));

        log.debug(String.format("expireDate_range: %s ~ %s",formExpDate, toExpDate));

        List<Coupon> coupons = couponService.findCouponsByExpireDateBetweenAndState(formExpDate, toExpDate, null).stream()
                                            .filter(f->!f.getState().equals(EnumCouponState.EXPIRE.getState()))
                                            .collect(Collectors.toList());
        coupons.forEach(coupon -> coupon.setState(EnumCouponState.EXPIRE.getState()));
        int expirationCount = couponService.updateALLCoupon(coupons).size();
        couponHistService.bulkInsertLog(coupons,EnumCouponState.EXPIRE);

        if(expirationCount!=coupons.size()){
            log.error("Coupons Filed to expire");
            completableFuture.complete(false);
            return completableFuture;
        }else{
            log.info(String.format("%d Coupons expired ",expirationCount));
            completableFuture.complete(true);
            return completableFuture;
        }

    }

    @Async(value = "taskSendMessageExecutor")
    public CompletableFuture<Long> notice3DaysExpirationCoupons(){

        CompletableFuture<Long> completableFuture = new CompletableFuture<>();

        Instant formExpDate = Instant.now().plus(Duration.ofDays(3)).truncatedTo(ChronoUnit.DAYS);
        Instant toExpireDate = formExpDate.plus(Duration.ofDays(1));

        log.debug(String.format("before3Day: %s ~ %s",formExpDate, toExpireDate));


        /**
         * Stream.parallel 의 고찰
         * paralle은 forkjoinpool이라는 common pool을 사용하는대, 이것은 i/o관련된 로직호출시 blocking이 발생되어 다른 thread에 영향이 있을수 있다.
         * Paralle()을 stream마다 사용한다면, 모든  paralle() 호출 로직에서 common pool을 사용하기 때문에 thread의 경합이 발생되어 성능저하에 영향이 커진다.
         * 따라서 forkjoinPool을 수동으로 선언하고 그 선언된 특정 forkJoinpool내에서 실행되도록 코드를 작성해야 한다.
         */

        //새로운 forkJoinPool을 생성한다.
        ForkJoinPool joinPool = new ForkJoinPool(5);
        List<String> codes = null;
        try {
            codes = joinPool.submit(()->couponService.findCouponsByExpireDateBetweenAndState(formExpDate, toExpireDate, EnumCouponState.ASSIGN.getState()).stream()
                                                .parallel()
                                                .map(p->{
                                                    return couponService.expireNotice(p.getCouponCode(), p.getUserId());
                                                }).collect(Collectors.toList())
            ).get();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            completableFuture.cancel(false);
            return completableFuture;
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            completableFuture.cancel(false);
            return completableFuture;
        }

        log.info(String.format("Notifications have been delivered to %d users", codes.size()));
        completableFuture.complete((long) codes.size());
        return completableFuture;
    }

}
