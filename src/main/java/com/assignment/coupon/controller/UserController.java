package com.assignment.coupon.controller;


import com.assignment.coupon.domain.dto.UserDto;
import com.assignment.coupon.domain.entity.User;
import com.assignment.coupon.service.impl.CustomUserDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        User user = customUserDetailService.createUser(userDto.getUserId(),
                                                    userDto.getPassword());

        return new ResponseEntity(user.getUsername(), HttpStatus.OK);

    }

    @GetMapping(value = "/auth-test")
    public ResponseEntity authTest(){
        return new ResponseEntity("security pass", HttpStatus.OK);
    }

    @GetMapping(value = "/authtoken-test")
    public ResponseEntity authTokenTest(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getSubject();
        return new ResponseEntity("security pass", HttpStatus.OK);
    }
}
