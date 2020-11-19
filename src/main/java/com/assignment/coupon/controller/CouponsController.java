package com.assignment.coupon.controller;

import com.assignment.coupon.domain.dto.IssueDto;
import com.assignment.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponsController extends BaseController{

    private final CouponService couponService;

    public CouponsController(CouponService couponService){
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity createCoupons(@RequestBody IssueDto issueDto){

        couponService.createCoupon(issueDto.getCount(), null);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userId}/assign")
    public ResponseEntity putAssignCoupons(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("couponCode")String couponCode,
                                           @PathVariable("userId")String userId){

        List<String> authorities = jwt.getClaim("authorities");

        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/bulk-assign")
    public ResponseEntity putBulkAssignCoupons(){
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userId}/use")
    public ResponseEntity putUseCoupons(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable("couponCode")String couponCode,
                                        @PathVariable("userId")String userId){

        List<String> authorities = jwt.getClaim("authorities");

        //admin 계정이면, userId = request userId 사용
        if(!hasRoleAdmin(jwt.getSubject(), userId, authorities)){
            //admin이 아니면 userId = token sub 사용
            userId = jwt.getSubject();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userId}/cancel")
    public ResponseEntity putCancelCoupons(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("couponCode")String couponCode,
                                           @PathVariable("userId")String userId){

        List<String> authorities = jwt.getClaim("authorities");

        //admin 계정이면, userId = request userId 사용
        if(!hasRoleAdmin(jwt.getSubject(), userId, authorities)){
            // userId = token sub 사용
            userId = jwt.getSubject();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/{couponCode}")
    public ResponseEntity getUsedCoupons(){
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping(value = "/expired-coupon")
    public ResponseEntity getExpiredCoupons(){
        return new ResponseEntity(HttpStatus.OK);
    }


}
