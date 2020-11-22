package com.assignment.coupon.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Validated
@RestController
public class TestController {

    //token통과 테스트 api
    @GetMapping(value = "/authtoken-test")
    public ResponseEntity authTokenTest(@AuthenticationPrincipal Jwt jwt,
                                        @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
                                        @RequestParam(value = "couponCode", required = false)String couponCode,
                                        @Size(max = 32)
                                        @Pattern(regexp = "^[\\w||-]*$")
                                        @RequestParam(value = "userName", required = false)String userName,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd")
                                        @RequestParam(value = "createDate", required = false)String selectedDate){
        String userId = jwt.getSubject();
        return new ResponseEntity("security pass", HttpStatus.OK);
    }
}
