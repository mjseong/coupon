package com.assignment.coupon.controller;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ConponController {

    private final CouponService couponService;

    @GetMapping("/api/coupon")
    public ResponseEntity getCoupon(){

        List<Coupon> couponList = couponService.findAll();

        return new ResponseEntity(couponList, HttpStatus.OK);
    }
}
