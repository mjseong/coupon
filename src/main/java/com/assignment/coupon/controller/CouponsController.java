package com.assignment.coupon.controller;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.exception.ErrorResponse;
import com.assignment.coupon.domain.dto.CouponCountDto;
import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.dto.IssueDto;
import com.assignment.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponsController extends BaseController{

    private final CouponService couponService;

    public CouponsController(CouponService couponService){
        this.couponService = couponService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity createCoupons(@RequestBody IssueDto issueDto){

        CouponCountDto couponCountDto= couponService.createCoupon(issueDto.getCount(), null);

        return new ResponseEntity(couponCountDto,HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userName}/assign")
    public ResponseEntity putAssignCoupons(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("couponCode")String couponCode,
                                           @PathVariable("userName")String userName){

        List<String> authorities = jwt.getClaim("authorities");
        CouponDto dto = couponService.assignCoupon(couponCode, userName);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @PutMapping(value = "/bulk-assign")
    public ResponseEntity putBulkAssignCoupons(){
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userName}/use")
    public ResponseEntity putUseCoupons(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable("couponCode")String couponCode,
                                        @PathVariable("userName")String userName){

        couponService.useCoupon(couponCode, userName);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userName}/cancel")
    public ResponseEntity putCancelCoupons(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("couponCode")String couponCode,
                                           @PathVariable("userName")String userName){

        couponService.cancelCoupon(couponCode, userName);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 잘못 생각 한것 정리 코
     */
//    @PutMapping(value = "/{couponCode}/use")
//    public ResponseEntity putUseCouponsByUser(@AuthenticationPrincipal Jwt jwt,
//                                        @PathVariable("couponCode")String couponCode){
//
//        //사용자 인증 토큰으로 사용자 이름 갖고옴
//        String userName = jwt.getSubject();
//        couponService.useCoupon(couponCode, userName);
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @PutMapping(value = "/{couponCode}/cancel")
//    public ResponseEntity putCancelCouponsByUser(@AuthenticationPrincipal Jwt jwt,
//                                           @PathVariable("couponCode")String couponCode){
//
//        //사용자 인증 토큰으로 사용자 이름 갖고옴
//        String userName = jwt.getSubject();
//
//        couponService.cancelCoupon(couponCode, userName);
//
//        return new ResponseEntity(HttpStatus.OK);
//    }

    //지급된 쿠폰 코드 조회
    @GetMapping(value = "/{couponCode}")
    public ResponseEntity getUsedCoupons(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable("couponCode")String couponCode){

        //사용자 인증 토큰으로 사용자 이름 갖고옴
        String userName = jwt.getSubject();

        List<CouponDto> coupons = couponService.findCouponsByUserId(userName);
        return new ResponseEntity(coupons, HttpStatus.OK);
    }


    @GetMapping(value = "/expired-coupon")
    public ResponseEntity getExpiredCoupons(@RequestParam("createDate")String selectedDate){

        Instant createDate = Instant.parse(selectedDate)
                        .truncatedTo(ChronoUnit.DAYS);

        couponService.findCouponsByCreateTodayAndExipiredState(createDate);

        return new ResponseEntity(HttpStatus.OK);
    }


}
