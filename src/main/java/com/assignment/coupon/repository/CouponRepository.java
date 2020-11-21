package com.assignment.coupon.repository;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCouponCode(String couponCode);
    Optional<Coupon> findCouponByCouponCodeAndUserIdIsNullAndState(String couponCode, String state);
    List<Coupon> findCouponsByUserIdAndState(String userId, String state);

    /**
     * select *
     * from coupon_info
     * where coupon_cdate >= ?
     * and coupon_cdate < ?
     * and coupon_state = 'EXPIRED';
     * @return
     */
    List<Coupon> findCouponsByCreateDateBetweenAndState(Instant fromCreateDate, Instant toCreateDate, String state, Pageable pageable);

    /**
     * select *
     * from coupon_info
     * where coupon_edate >= ? (system.DAY + 3)
     * and coupon_edate < ? (system.DAY + 3)
     * and coupon_state =  ? ('ASSIGNED');
     * @return
     */
    List<Coupon> findCouponsByExpireDateBetweenAndState(Instant fromExpireDate, Instant toExpireDate, String state);


    List<Coupon> findCouponsByExpireDateBetween(Instant fromExpireDate, Instant toExpireDate);


}
