package com.assignment.coupon.repository;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCouponCode(String couponCode);
    Optional<Coupon> findCouponByCouponCodeAndUserIdIsNullAndState(String couponCode, String state);
    List<Coupon> findCouponsByUserIdAndState(String userId, String state);
//    List<Coupon> findCouponsByStateAndUserId();







}
