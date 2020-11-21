package com.assignment.coupon.controller;

import com.assignment.coupon.domain.dto.CouponCountDto;
import com.assignment.coupon.domain.dto.CouponDto;
import com.assignment.coupon.domain.dto.IssueDto;
import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.domain.state.EnumCouponState;
import com.assignment.coupon.service.CouponService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Validated
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

        List<Coupon> coupons = couponService.createCoupon(issueDto.getCount(), null);

        return new ResponseEntity(new CouponCountDto(coupons.size()),HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userName}/assign")
    public ResponseEntity putAssignCoupons(@AuthenticationPrincipal Jwt jwt,
                                           @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
                                           @PathVariable("couponCode")String couponCode,
                                           @NotBlank
                                           @Size(max = 32)
                                           @Pattern(regexp = "^[\\w||-]*$")
                                           @PathVariable("userName")String userName){

        List<String> authorities = jwt.getClaim("authorities");
        CouponDto coupon = couponService.assignCoupon(couponCode, userName);

        return new ResponseEntity(coupon, HttpStatus.OK);
    }

    @PutMapping(value = "/bulk-assign")
    public ResponseEntity putBulkAssignCoupons(){
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userName}/use")
    public ResponseEntity putUseCoupons(@AuthenticationPrincipal Jwt jwt,
                                        @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
                                        @PathVariable("couponCode")String couponCode,
                                        @NotBlank
                                        @Size(max = 32)
                                        @Pattern(regexp = "^[\\w||-]*$")
                                        @PathVariable("userName")String userName){

        CouponDto coupon = couponService.useCoupon(couponCode, userName);

        return new ResponseEntity(coupon, HttpStatus.OK);
    }

    @PutMapping(value = "/{couponCode}/users/{userName}/cancel")
    public ResponseEntity putCancelCoupons(@AuthenticationPrincipal Jwt jwt,
                                           @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
                                           @PathVariable("couponCode")String couponCode,
                                           @NotBlank
                                           @Size(max = 32)
                                           @Pattern(regexp = "^[\\w||-]*$")
                                           @PathVariable("userName")String userName){

        CouponDto coupon = couponService.cancelCoupon(couponCode, userName);

        return new ResponseEntity(coupon, HttpStatus.OK);
    }

    //지급된 쿠폰 조회
    @GetMapping(value = "/user")
    public ResponseEntity getUserCoupons(@AuthenticationPrincipal Jwt jwt){

        //사용자 인증 토큰으로 사용자 이름 갖고옴
        String userName = jwt.getSubject();

        List<CouponDto> coupons = couponService.findCouponsByUserId(userName);
        return new ResponseEntity(coupons, HttpStatus.OK);
    }

    //지급된 쿠폰 코드 조회
    @GetMapping(value = "/{couponCode}")
    public ResponseEntity getUserCoupon(@Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
                                        @PathVariable("couponCode")String couponCode){

         CouponDto coupon = couponService.findByCouponCodeAndStateAndUserNotNull(couponCode, EnumCouponState.ASSIGN.getState());
        return new ResponseEntity(coupon, HttpStatus.OK);
    }

    //발급코드 당일 만료건 조회
    @GetMapping(value = "/expired-coupon")
    public ResponseEntity getExpiredCoupons(@DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam("searchDate")String searchDate,
                                            Pageable pageable){

        LocalDate date = LocalDate.parse(searchDate);
        Instant createDate = date.atStartOfDay(ZoneId.systemDefault())
                                            .toInstant().plus(Duration.ofDays(1))
                                            .truncatedTo(ChronoUnit.DAYS);

        List<CouponDto> coupons = couponService.findCouponsByCreateTodayAndExipiredState(createDate, pageable);

        return new ResponseEntity(coupons, HttpStatus.OK);
    }


}
