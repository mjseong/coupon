package com.assignment.coupon.service;

import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.entity.Coupon;

import java.time.Instant;
import java.util.List;

public interface CouponService {

    public List<Coupon> createCoupon(long count, Instant expDate);

    public String assignCoupon(String couponCode, String userId);

    public List<String> bulkAssignCoupon(List<String> couponCodes, String userId);

    public List<CouponDto> findCouponsByUserId(String userId);

    public CouponDto useCoupon(String couponCode);

    public CouponDto cancelCoupon(String couponCode);


}
