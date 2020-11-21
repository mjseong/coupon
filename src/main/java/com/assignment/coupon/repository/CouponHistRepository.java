package com.assignment.coupon.repository;

import com.assignment.coupon.domain.entity.CouponHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface CouponHistRepository extends JpaRepository<CouponHist, Long> {

    @Query(nativeQuery = true,
            value = "INSERT INTO coupon_hist(coupon_code, " +
                    "coupon_state, coupon_event, user_id, coupon_issuer," +
                    "coupon_cdate, coupon_edate, coupon_udate) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)")
    public CouponHist insertCouponLog(String couponCode,
                                                String state,
                                                String event,
                                                String userName,
                                                String issuer,
                                                Instant createDate,
                                                Instant expireDate,
                                                Instant updateDate);
}
