package com.assignment.coupon.service;

import com.assignment.coupon.domain.dto.CouponCountDto;
import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.entity.Coupon;

import java.time.Instant;
import java.util.List;

public interface CouponService {

    public CouponCountDto createCoupon(long count, Instant expDate);

    public CouponDto assignCoupon(String couponCode, String userId);

    public List<String> bulkAssignCoupon(List<String> couponCodes, String userId);

    public CouponDto useCoupon(String couponCode, String userId);

    public CouponDto cancelCoupon(String couponCode, String userId);

    public Coupon updateCouponState(Coupon coupon, String state);

    public Coupon findByCouponCodeAndStateAndUserNotNull(String couponCode, String state);

    public List<CouponDto> findCouponsByUserId(String userId);


}
