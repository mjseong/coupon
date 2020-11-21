package com.assignment.coupon.service;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;

import java.util.List;

public interface CouponHistService {

    public long bulkInsertLog(List<Coupon> coupons, EnumCouponState couponState);
    public long insertLog(Coupon coupon, EnumCouponState couponState);
}
