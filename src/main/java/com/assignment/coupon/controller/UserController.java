package com.assignment.coupon.controller;


import com.assignment.coupon.domain.dto.UserDto;
import com.assignment.coupon.domain.entity.User;
import com.assignment.coupon.service.impl.CustomUserDetailService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Validated
@RestController
public class UserController {

    private final CustomUserDetailService customUserDetailService;

    public UserController(CustomUserDetailService customUserDetailService){
        this.customUserDetailService = customUserDetailService;
    }

    @PostMapping(value = "/signup")
    @ResponseBody
    public ResponseEntity signUp(@RequestBody @Valid UserDto userDto){

        User user = customUserDetailService.createUser(userDto.getUserName(),
                                                    userDto.getPassword(),
                                                    userDto.getAdminRole());

        return new ResponseEntity(user.getUsername(), HttpStatus.OK);

    }

    @GetMapping(value = "/auth-test")
    public ResponseEntity authTest(){
        return new ResponseEntity("security pass", HttpStatus.OK);
    }

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
