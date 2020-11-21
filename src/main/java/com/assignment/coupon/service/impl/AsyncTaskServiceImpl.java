package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.service.AsyncTaskService;
import com.assignment.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsyncTaskServiceImpl implements AsyncTaskService {

    private final CouponService couponService;

    public AsyncTaskServiceImpl(CouponService couponService){
        this.couponService = couponService;
    }

    @Async
    public void expireCoupons(){

        Instant toExpDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant formExpDate = toExpDate.minus(Duration.ofDays(3));

        log.info(String.format("expireDate_range: %s ~ %s",formExpDate, toExpDate));

        List<Coupon> coupons = couponService.findCouponsByExpireDateBetweenAndState(formExpDate, toExpDate, null).stream()
                                            .filter(f->!f.getState().equals(EnumCouponState.EXPIRE.getState()))
                                            .collect(Collectors.toList());
        coupons.forEach(coupon -> coupon.setState(EnumCouponState.EXPIRE.getState()));
        int expirationCount = couponService.updateALLCoupon(coupons).size();

        if(expirationCount!=coupons.size()){
            log.error("Coupons Filed to expire");
        }else{
            log.info(String.format("%d Coupons expired ",expirationCount));
        }

    }

    @Async
    public void notice3DaysExpirationCoupons(){
        Instant formExpDate = Instant.now().plus(Duration.ofDays(3)).truncatedTo(ChronoUnit.DAYS);
        Instant toExpireDate = formExpDate.plus(Duration.ofDays(1));

        log.info(String.format("before3Day: %s ~ %s",formExpDate, toExpireDate));

        List<String> codes = couponService.findCouponsByExpireDateBetweenAndState(formExpDate, toExpireDate, EnumCouponState.ASSIGN.getState()).stream()
                                        .map(p->{
                                            return couponService.expireNotice(p.getCouponCode(), p.getUserId());
                                        }).collect(Collectors.toList());

        log.info(String.format("Notifications have been delivered to %d users", codes.size()));
    }

}