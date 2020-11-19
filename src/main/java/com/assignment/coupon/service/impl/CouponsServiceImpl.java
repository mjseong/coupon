package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.repository.CouponRepository;
import com.assignment.coupon.service.CouponService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CouponsServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponsServiceImpl(CouponRepository couponRepository){
        this.couponRepository = couponRepository;
    }

    @Override
    public List<String> createCoupon(long count) {
       return Stream
               .generate(()->couponRepository.save(Coupon.newCoupon("")).getCouponCode())
               .limit(count)
                .collect(Collectors.toList());
    }
}
