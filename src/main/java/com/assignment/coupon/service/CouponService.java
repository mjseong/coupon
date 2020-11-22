package com.assignment.coupon.service;

import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.entity.Coupon;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface CouponService {

    public List<Coupon>createCoupon(long count, Instant expDate);

    public CouponDto assignCoupon(String couponCode, String userId);

//    public List<String> bulkAssignCoupon(List<String> couponCodes, String userId);

    public CouponDto useCoupon(String couponCode, String userId);

    public CouponDto cancelCoupon(String couponCode, String userId);

    public Coupon updateCouponState(Coupon coupon, String state);

    public List<Coupon> updateALLCoupon(List<Coupon> coupons);

    public CouponDto findByCouponCodeAndStateAndUserNotNull(String couponCode, String state);

    public List<CouponDto> findCouponsByUserId(String userId);

    public List<CouponDto> findCouponsByCreateTodayAndExipiredState(Instant today, Pageable pageable);

    public List<Coupon> findCouponsByExpireDateBetweenAndState(Instant fromExpireDate, Instant toExpireDate, String state);

    public String expireNotice(String couponCode, String userId);



}
